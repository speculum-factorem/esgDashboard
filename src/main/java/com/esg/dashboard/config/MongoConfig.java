package com.esg.dashboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@Slf4j
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.esg.dashboard.repository")
public class MongoConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("esg-dashboard-system");
    }

    public MongoConfig() {
        log.info("MongoDB configuration initialized");
    }
}