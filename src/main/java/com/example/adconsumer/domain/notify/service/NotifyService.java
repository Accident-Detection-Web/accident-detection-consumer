package com.example.adconsumer.domain.notify.service;


import com.example.adconsumer.domain.notify.entity.Notify;
import com.example.adconsumer.domain.notify.repository.EmitterRepository;
import com.example.adconsumer.domain.notify.repository.NotifyRepository;

import com.example.adconsumer.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;


import static com.example.adconsumer.domain.notify.dto.NotifyDto.Response.createResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotifyService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotifyRepository notifyRepository;

    @Transactional
    public SseEmitter subscribe(String username, String lastEventId) {
        String emitterId = makeTimeIncludeId(username);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러 방지를 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(username);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userEmail=" + username + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, username, emitterId, emitter);
        }

        return emitter;
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId,
        SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
            .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
            .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(eventId)
                .name("sse")
                .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    @Transactional
    public void send(User receiver, Notify.NotificationType notificationType, String content,
                     String url) {
        Notify notification = notifyRepository.save(createNotification(receiver, notificationType, content, url));

        String receiverEmail = receiver.getEmail();
        String eventId = receiverEmail + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverEmail);
        emitters.forEach(
            (key, emitter) ->{
                emitterRepository.saveEventCache(key, notification);
                sendNotification(emitter, eventId, key, createResponse(notification));
            }
        );

    }

    private Notify createNotification(User receiver, Notify.NotificationType notificationType,
        String content, String url) {
        return Notify.builder()
            .receiver(receiver)
            .notificationType(notificationType)
            .content(content)
            .url(url)
            .isRead(false)
            .build();
    }
}
