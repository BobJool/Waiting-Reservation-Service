package com.bobjool.notification.infrastructure.repository;

import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.Notification;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * QueryDSL 인터페이스
 */
public interface NotificationRepositoryCustom {
    Page<Notification> search(
            Long userId,
            String message,
            LocalDate date,
            BobjoolServiceType category,
            NotificationType action,
            NotificationChannel channel,
            Pageable pageable
    );
}
