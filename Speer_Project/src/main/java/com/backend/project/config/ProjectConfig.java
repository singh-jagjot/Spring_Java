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
    private Long secsToExpire;
    private Boolean disableRateLimiting;
}
