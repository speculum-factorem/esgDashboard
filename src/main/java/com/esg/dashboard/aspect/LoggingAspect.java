package com.esg.dashboard.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.UUID;

/**
 * Аспект для логирования выполнения методов контроллеров и сервисов
 * Измеряет время выполнения и логирует все вызовы методов
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.esg.dashboard.controller..*(..)) || " +
            "execution(* com.esg.dashboard.service..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        MDC.put("traceId", traceId);
        MDC.put("method", methodName);
        MDC.put("class", className);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            log.debug("Method execution started - Arguments: {}", Arrays.toString(joinPoint.getArgs()));

            Object result = joinPoint.proceed();

            stopWatch.stop();
            log.debug("Method executed successfully - Execution time: {} ms", stopWatch.getTotalTimeMillis());

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("Error executing method - Error: {} - Execution time: {} ms",
                    e.getMessage(), stopWatch.getTotalTimeMillis(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}