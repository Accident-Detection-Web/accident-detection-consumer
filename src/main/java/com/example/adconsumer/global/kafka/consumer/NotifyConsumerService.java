package com.example.adconsumer.global.kafka.consumer;

import com.example.adconsumer.domain.notify.dto.NotifyMessage;
import com.example.adconsumer.domain.notify.entity.Notify;
import com.example.adconsumer.domain.notify.service.NotifyService;
import com.example.adconsumer.domain.user.entity.User;
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

        User user = objectMapper.readValue(data.value().toString(), User.class);
        notifyService.send(user, Notify.NotificationType.ACCIDENT, NotifyMessage.ACCIDENT_DETECTION.getMessage(), null);

        log.debug("컨슈머 알림 처리 elapsed time : "  + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
