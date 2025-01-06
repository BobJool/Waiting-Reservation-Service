//package com.bobjool.auth.application.presentation.controller;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@TestConfiguration
//@EnableWebSecurity
//public class SecurityConfigTest {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        // 모든 요청 허용
//        http.csrf((csrf) -> csrf.disable());
//
//        http.authorizeRequests().anyRequest().permitAll();
//
//        return http.build();
//    }
//}