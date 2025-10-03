package com.pulse.canvas.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.UUID;

@Configuration
public class AppConfig {

    @Bean
    public String appInstanceId() {
        return UUID.randomUUID().toString();
    }
}