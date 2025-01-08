package com.bobjool.notification.infrastructure.repository;

import com.bobjool.notification.domain.entity.Ai;
import com.bobjool.notification.domain.repository.AiRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 데이터 접근 인터페이스
 */
public interface AiRepositoryImpl
        extends JpaRepository<Ai, UUID>, AiRepository, AiRepositoryCustom {
}
