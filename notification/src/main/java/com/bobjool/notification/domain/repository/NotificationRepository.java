package com.bobjool.notification.domain.repository;

import com.bobjool.notification.domain.entity.Notification;

/**
 * 도메인 관점 인터페이스
 */
public interface NotificationRepository {
    Notification save(Notification notification);
}
