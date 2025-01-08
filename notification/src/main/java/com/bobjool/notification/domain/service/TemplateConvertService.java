package com.bobjool.notification.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TemplateConvertService {
    /**
     * 문자열 날짜의 포맷을 변경합니다.
     * @param date 2025-01-03
     * @return 2025년 1월 3일
     */
    public String formatLocalDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN);
        return localDate.format(formatter);
    }

    /**
     * 문자열 시간의 포맷을 변경합니다.
     * @param time 20:45
     * @return 오후 8시 45분
     */
    public String formatLocalTime(String time) {
        LocalTime localTime = LocalTime.parse(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh시 mm분", Locale.KOREAN);
        return localTime.format(formatter);
    }

    /**
     * 천원 단위에 쉼표를 추가합니다.
     * @param money 40000
     * @return 40,000
     */
    public String formatMoney(Integer money){
        return String.format("$%, d", money);
    }

    public String templateBinding(String template, Map<String, String> variables) {
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            template = template.replace("${"+variable.getKey()+"}", variable.getValue());
        }
        return template;
    }
    public String setTitleBold(String title) {
        return new StringBuilder()
                .append("*")
                .append(title)
                .append("*")
                .append("\n")
                .toString();
    }

    public String getMapToJsonString(Map<String, String> map) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonBuilder.append("\"");
            jsonBuilder.append(entry.getKey());
            jsonBuilder.append("\": \"");
            jsonBuilder.append(entry.getValue());
            jsonBuilder.append("\",");
        }
        jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        jsonBuilder.append("}");
        return jsonBuilder.toString();
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
