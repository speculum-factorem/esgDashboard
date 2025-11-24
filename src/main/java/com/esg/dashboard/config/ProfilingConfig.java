package com.esg.dashboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
@Profile("dev")
public class ProfilingConfig {

    @PostConstruct
    public void init() {
        log.info("Development profiling configuration loaded");
        log.info("JMX monitoring enabled on port 9091");
        log.info("Flight recorder enabled for performance analysis");
    }
}