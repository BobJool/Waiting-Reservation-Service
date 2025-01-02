package com.bobjool.notification.infrastructure.repository;

import com.bobjool.notification.domain.entity.Template;
import com.bobjool.notification.domain.repository.TemplateRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 데이터 접근 인터페이스
 */
public interface TemplateRepositoryImpl
        extends JpaRepository<Template, UUID>, TemplateRepository {

}
