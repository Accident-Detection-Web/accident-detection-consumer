package com.example.adconsumer.domain.notify.entity;

import com.example.adconsumer.domain.user.entity.User;
import com.example.adconsumer.global.entity.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notify extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String content;

    private String url;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    // @Embedded
    // private NotificationContent content;

    // @Embedded
    // private RelatedURL url;

    @Builder
    public Notify(User receiver, NotificationType notificationType, String content, String url, Boolean isRead){
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }


    public enum NotificationType{
        ACCIDENT
    }
}
