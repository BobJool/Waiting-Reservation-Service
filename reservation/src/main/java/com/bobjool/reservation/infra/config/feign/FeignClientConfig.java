package com.bobjool.reservation.infra.config.feign;

import feign.Client;
import feign.httpclient.ApacheHttpClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignClientConfig {
    @Bean
    public Client client() {
        return new ApacheHttpClient();
    }
}
