package com.bobjool.notification.domain.repository;

import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.Notification;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * 도메인 관점 인터페이스
 */
public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(UUID id);

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
