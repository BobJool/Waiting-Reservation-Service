package com.bobjool.notification.infrastructure.config.template;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "notification.template-mapping")
@Getter
@Setter
public class TemplateMappingConfig {
    private Map<String, String> queue;
    private Map<String, String> reservation;
}
