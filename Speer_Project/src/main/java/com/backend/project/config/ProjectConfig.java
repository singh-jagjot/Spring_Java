package com.backend.project.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "project")
public class ProjectConfig {
    private String secret;
    private Long expiryDurationSecs;
    private Boolean disableRateLimiting;
    private Long bucketCapacity;
    private Long refillRate;
    private Long refillPeriod;
}
