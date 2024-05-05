package com.example.adconsumer.domain.notify.dto;


import com.example.adconsumer.domain.notify.entity.Notify;
import lombok.*;

public class NotifyDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Response{
        String id;
        String name;
        String content;
        String type;
        String createdAt;

        public static Response createResponse(Notify notify) {
            return Response.builder()
                    .content(notify.getContent())
                    .id(notify.getId().toString())
                    .name(notify.getReceiver().getUsername())
                    .createdAt(notify.getCreatedAt().toString())
                    .build();
        }
    }
}
