package com.bobjool.notification.application.service;

import com.bobjool.notification.application.dto.TemplateCreateDto;
import com.bobjool.notification.application.dto.TemplateDto;
import com.bobjool.notification.domain.entity.Template;
import com.bobjool.notification.domain.repository.TemplateRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {
    private final Gson gson;
    private final TemplateRepository templateRepository;

    public List<TemplateDto> selectTemplate() {
        List<Template> templateList = templateRepository.findAllByDeletedAtIsNull();
        return templateList.stream()
                .map(TemplateDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TemplateDto createTemplate(TemplateCreateDto createDto) {
        String variables = this.toJsonString(
                this.extractVariables(
                        createDto.template()
                )
        );
        log.info("Key values used in the template: {}", variables);

        Template template = Template.createTemplate(
                createDto.serviceType(),
                createDto.channel(),
                createDto.type(),
                createDto.title(),
                createDto.template(),
                variables
        );

        return TemplateDto.from(
                templateRepository.save(template)
        );
    }

    /**
     * 변수 들을 Json 형태의 문자열로 변환합니다.
     * ex: ["name", "place", "id"]
     */
    private String toJsonString(List<String> variables) {
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.toJson(variables, type);
    }

    /**
     * 표현식을 포함하는 문자열에서 key 를 추출합니다.
     *
     * @param template ${key} 형태를 포함하는 문자열
     * @return key List
     */
    private List<String> extractVariables(String template) {
        Pattern pattern = Pattern.compile("\\$\\{(.*?)}");
        Matcher matcher = pattern.matcher(template);

        List<String> variables = new ArrayList<>();

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }

        return variables;
    }

}
