package com.bobjool.notification.domain.entity;

import com.bobjool.common.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_notification")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", nullable = false)
    private Template templateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "contact")
    private String contact;

    @Column(name = "json_data", length = 1024)
    private String jsonData;

    @Column(name = "message", nullable = false, length = 1024)
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    public static Notification createNotification(Template templateId, Long userId, String jsonData, String message, String contact) {
        return Notification.builder()
                .templateId(templateId)
                .userId(userId)
                .jsonData(jsonData)
                .message(message)
                .contact(contact)
                .status(NotificationStatus.PENDING)
                .build();
    }

    public void updateStatus(NotificationStatus status) {
        this.status = status;
    }

}
