package com.bobjool.notification.domain.entity;

import com.bobjool.common.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_notification_template")
public class Template extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BobjoolServiceType serviceType;

    @Column(name = "channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "title")
    private String title;

    @Column(name = "template", nullable = false)
    private String template;

    @Column(name = "variables")
    private String variables;

    public static Template createTemplate(
            BobjoolServiceType serviceType,
            NotificationChannel channel,
            NotificationType type,
            String title,
            String template,
            String variables
    ) {
        return Template.builder()
                .serviceType(serviceType)
                .channel(channel)
                .type(type)
                .title(title)
                .template(template)
                .variables(variables)
                .build();
    }

    public void updateTemplate(
            BobjoolServiceType serviceType,
            NotificationChannel channel,
            NotificationType type,
            String title,
            String template,
            String variables
    ) {
        this.serviceType = serviceType;
        this.channel = channel;
        this.type = type;
        this.title = title;
        this.template = template;
        this.variables = variables;
    }

}
