package com.bobjool.queue.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()); // JavaTimeModule 등록

    // static 메서드만 제공하는 유틸 클래스니까 생성자를 노출할 필요가 없다.
    private EventSerializer() {}

    // 직렬화 (객체 -> JSON 문자열)
    public static <T> String serialize(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object: {}", object, e);
            throw new RuntimeException("Serialization error", e);
        }
    }

    // 역직렬화 (JSON 문자열 -> 객체)
    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON: {}", json, e);
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
