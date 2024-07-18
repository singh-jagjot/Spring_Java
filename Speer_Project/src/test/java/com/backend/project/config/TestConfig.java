package com.backend.project.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public ProjectConfig projectConfig() {
        ProjectConfig config = new ProjectConfig();
//        config.setBucketCapacity(10);
//        config.setRefillRate(1);
//        config.setRefillPeriod(1);
//        config.setDisableRateLimiting(true);
        return config;
    }
}
