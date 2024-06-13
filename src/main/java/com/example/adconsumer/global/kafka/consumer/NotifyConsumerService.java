package com.example.adconsumer.global.kafka.consumer;

import com.example.adconsumer.domain.notify.dto.NotifyMessage;
import com.example.adconsumer.domain.notify.entity.Notify;
import com.example.adconsumer.domain.notify.service.NotifyService;
import com.example.adconsumer.global.kafka.dto.NotificationDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotifyConsumerService {

    private final ObjectMapper objectMapper;
    private final NotifyService notifyService;

    @KafkaListener(topics = "notify-1", groupId = "group-1")
    public void listener(ConsumerRecord data) throws JsonProcessingException {

        log.info(data.toString());
        long startTime = System.currentTimeMillis();

        NotificationDataDto notificationData = objectMapper.readValue(data.value().toString(), NotificationDataDto.class);

        // isFirst가 true이면, 구독 시작
        if (notificationData.getIsFirst()) {
            notifyService.subscribe(notificationData.getUser().getUsername(), notificationData.getLastEventId());
            log.info("Started new subscription for user: {}", notificationData.getUser().getUsername());
        } else { // isFirst가 false이면, 기존 구독자에게 알림 전송
            notifyService.send(notificationData.getUser(), Notify.NotificationType.ACCIDENT, NotifyMessage.ACCIDENT_DETECTION.getMessage(), null);
            log.info("Sent notification to existing subscriber: {}", notificationData.getUser().getUsername());
        }

//        User user = objectMapper.readValue(data.value().toString(), User.class);
//        notifyService.send(user, Notify.NotificationType.ACCIDENT, NotifyMessage.ACCIDENT_DETECTION.getMessage(), null);

        log.debug("컨슈머 알림 처리 elapsed time : "  + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
