package com.example.adconsumer.global.kafka.dto;

import com.example.adconsumer.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDataDto {
    private User user;
    private Boolean isFirst;
    private String lastEventId;

}
