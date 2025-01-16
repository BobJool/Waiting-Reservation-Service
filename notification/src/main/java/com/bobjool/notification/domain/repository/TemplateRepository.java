package com.bobjool.notification.domain.repository;

import com.bobjool.notification.domain.entity.Template;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 도메인 관점 인터페이스
 */
public interface TemplateRepository {
    Template save(Template template);

    List<Template> findAllByDeletedAtIsNull();

    Optional<Template> findByIdAndDeletedAtIsNull(UUID id);
}
