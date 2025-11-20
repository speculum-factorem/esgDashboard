package com.esg.dashboard.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MonitoringAspect {

    private final MeterRegistry meterRegistry;

    @Around("execution(* com.esg.dashboard.service..*(..))")
    public Object monitorServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Timer.Sample sample = Timer.start(meterRegistry);
        String status = "success";

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            status = "error";
            throw e;
        } finally {
            sample.stop(Timer.builder("esg.service.method.duration")
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("status", status)
                    .register(meterRegistry));

            // Count method calls
            meterRegistry.counter("esg.service.method.calls",
                    "class", className,
                    "method", methodName,
                    "status", status).increment();
        }
    }

    @Around("execution(* com.esg.dashboard.controller..*(..))")
    public Object monitorControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Timer.Sample sample = Timer.start(meterRegistry);
        String status = "success";

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            status = "error";
            throw e;
        } finally {
            sample.stop(Timer.builder("esg.controller.method.duration")
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("status", status)
                    .register(meterRegistry));
        }
    }
}