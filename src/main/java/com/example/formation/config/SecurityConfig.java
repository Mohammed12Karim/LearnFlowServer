package com.example.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.formation.data.security.PasswordUtil;

@Configuration
public class SecurityConfig {

        @Bean
        public PasswordUtil passwordEncoder() {
                return new PasswordUtil();
        }
}
