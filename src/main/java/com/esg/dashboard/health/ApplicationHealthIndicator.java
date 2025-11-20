package com.esg.dashboard.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationHealthIndicator implements HealthIndicator {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    @Override
    public Health health() {
        try {
            long uptime = runtimeMXBean.getUptime();
            long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
            double heapUsagePercent = (double) heapUsed / heapMax * 100;

            Health.Builder status = Health.up();

            if (heapUsagePercent > 90) {
                status = Health.down();
            } else if (heapUsagePercent > 80) {
                status = Health.outOfService();
            }

            return status
                    .withDetail("uptime", formatUptime(uptime))
                    .withDetail("heapUsed", heapUsed / 1024 / 1024 + " MB")
                    .withDetail("heapMax", heapMax / 1024 / 1024 + " MB")
                    .withDetail("heapUsage", String.format("%.2f%%", heapUsagePercent))
                    .withDetail("threadCount", Thread.activeCount())
                    .withDetail("timestamp", LocalDateTime.now().toString())
                    .build();

        } catch (Exception e) {
            log.error("Application health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now().toString())
                    .build();
        }
    }

    private String formatUptime(long uptime) {
        long days = uptime / (1000 * 60 * 60 * 24);
        long hours = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (uptime % (1000 * 60)) / 1000;

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }
}