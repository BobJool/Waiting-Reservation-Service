package com.bobjool.notification.infrastructure.repository;

import com.bobjool.notification.domain.entity.Notification;
import com.bobjool.notification.domain.repository.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 데이터 접근 인터페이스
 */
public interface AiRepositoryImpl
        extends JpaRepository<Notification, UUID>, NotificationRepository, NotificationRepositoryCustom {
}
