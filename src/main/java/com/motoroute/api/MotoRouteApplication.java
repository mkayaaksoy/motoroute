package com.motoroute.api;

import com.motoroute.api.infrastructure.config.JwtProperties;
import com.motoroute.api.infrastructure.config.OpenRouteProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableConfigurationProperties({JwtProperties.class, OpenRouteProperties.class})
public class MotoRouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotoRouteApplication.class, args);
    }
}
