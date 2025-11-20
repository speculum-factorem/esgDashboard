package com.esg.dashboard.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceMonitor {

    private final MeterRegistry meterRegistry;
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void monitorPerformance() {
        try {
            // Memory usage
            long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
            double heapUsagePercent = (double) heapUsed / heapMax * 100;

            // Thread count
            int threadCount = threadMXBean.getThreadCount();

            // Record metrics
            meterRegistry.gauge("app.memory.heap.used", heapUsed);
            meterRegistry.gauge("app.memory.heap.usage.percent", heapUsagePercent);
            meterRegistry.gauge("app.threads.count", threadCount);

            log.debug("Performance metrics - Heap: {}%, Threads: {}",
                    String.format("%.2f", heapUsagePercent), threadCount);

        } catch (Exception e) {
            log.warn("Failed to collect performance metrics: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 60000) // Every minute
    public void logPerformanceSummary() {
        if (log.isInfoEnabled()) {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            log.info("Memory Summary - Used: {} MB, Total: {} MB, Max: {} MB",
                    usedMemory / 1024 / 1024,
                    totalMemory / 1024 / 1024,
                    maxMemory / 1024 / 1024);
        }
    }
}