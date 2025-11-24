package com.esg.dashboard.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Мониторинг производительности приложения
 * Собирает метрики использования памяти и потоков, публикует их в Micrometer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceMonitor {

    private final MeterRegistry meterRegistry;
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @Scheduled(fixedRate = 30000) // Каждые 30 секунд
    public void monitorPerformance() {
        try {
            MDC.put("operation", "MONITOR_PERFORMANCE");
            
            // Использование памяти
            long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
            double heapUsagePercent = heapMax > 0 ? (double) heapUsed / heapMax * 100 : 0.0;

            // Количество потоков
            int threadCount = threadMXBean.getThreadCount();

            // Записываем метрики в Micrometer
            meterRegistry.gauge("app.memory.heap.used", heapUsed);
            meterRegistry.gauge("app.memory.heap.usage.percent", heapUsagePercent);
            meterRegistry.gauge("app.threads.count", threadCount);

            log.debug("Performance metrics - Memory: {}%, Threads: {}",
                    String.format("%.2f", heapUsagePercent), threadCount);

        } catch (Exception e) {
            log.warn("Error collecting performance metrics: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    @Scheduled(fixedRate = 60000) // Каждую минуту
    public void logPerformanceSummary() {
        try {
            MDC.put("operation", "LOG_PERFORMANCE_SUMMARY");
            
            if (log.isInfoEnabled()) {
                Runtime runtime = Runtime.getRuntime();
                long maxMemory = runtime.maxMemory();
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;

                log.info("Memory summary - Used: {} MB, Total: {} MB, Max: {} MB",
                        usedMemory / 1024 / 1024,
                        totalMemory / 1024 / 1024,
                        maxMemory / 1024 / 1024);
            }
        } finally {
            MDC.clear();
        }
    }
}