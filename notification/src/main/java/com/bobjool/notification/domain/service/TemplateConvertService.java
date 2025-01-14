package com.bobjool.notification.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TemplateConvertService {
    public String templateBinding(String template, Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            log.warn("variables is null or empty");
            return template;
        }
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            template = template.replace("${" + variable.getKey() + "}", variable.getValue() == null ? "" : variable.getValue());
        }
        return template;
    }

    public String getVariablesToJson(String serializeTemplate) {
        String variables = this.toJsonString(
                this.extractVariables(
                        serializeTemplate
                )
        );
        log.info("converted template: {} to variables: {}", serializeTemplate, variables);

        return variables;
    }

    /**
     * 변수 들을 Json 형태의 문자열로 변환합니다.
     * ex: ["name", "place", "id"]
     */
    private String toJsonString(List<String> variables) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("[");
        for (int i = 0; i < variables.size(); i++) {
            jsonBuilder
                    .append("\"")
                    .append(variables.get(i))
                    .append("\"");
            if (i < variables.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        return jsonBuilder.toString();
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
