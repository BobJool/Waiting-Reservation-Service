package com.bobjool.notification.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.notification.application.dto.TemplateCreateDto;
import com.bobjool.notification.application.dto.TemplateDto;
import com.bobjool.notification.domain.entity.Template;
import com.bobjool.notification.domain.repository.TemplateRepository;
import com.bobjool.notification.domain.service.TemplateConvertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateConvertService templateConvertService;

    public List<TemplateDto> selectTemplate() {
        List<Template> templateList = templateRepository.findAllByDeletedAtIsNull();
        log.info("selected templates: {}", templateList);

        return templateList.stream()
                .map(TemplateDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TemplateDto createTemplate(TemplateCreateDto createDto) {
        Template template = Template.createTemplate(
                createDto.serviceType(),
                createDto.channel(),
                createDto.type(),
                createDto.title(),
                createDto.template(),
                templateConvertService.getVariablesToJson(
                        createDto.template()
                )
        );
        log.info("created template: {}", template);

        return TemplateDto.from(
                templateRepository.save(template)
        );
    }

    @Transactional
    public TemplateDto updateTemplate(UUID templatesId, TemplateCreateDto createDto) {
        Template template = getTemplateById(templatesId);
        String variables = templateConvertService.getVariablesToJson(
                createDto.template()
        );

        template.updateTemplate(
                createDto.serviceType(),
                createDto.channel(),
                createDto.type(),
                createDto.title(),
                createDto.template(),
                variables
        );
        log.info("updated template: {}", template);

        return TemplateDto.from(template);
    }

    private Template getTemplateById(UUID templatesId) {
        return templateRepository.findByIdAndDeletedAtIsNull(templatesId)
    @Transactional
    public TemplateDto deleteTemplate(UUID templateId) {
        Template template = getTemplateById(templateId);

        template.deleteTemplate();
        log.info("deleted template: {}", template);

        return TemplateDto.from(template);
    }

                .orElseThrow(
                        () -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND)
                );
    }

}
