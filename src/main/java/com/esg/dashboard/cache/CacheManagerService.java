package com.esg.dashboard.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheManagerService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COMPANY_CACHE_PREFIX = "company:";
    private static final String PORTFOLIO_CACHE_PREFIX = "portfolio:";
    private static final String RANKING_KEY = "esg:ranking";

    public void evictCompanyCache(String companyId) {
        try {
            org.slf4j.MDC.put("companyId", companyId);
            org.slf4j.MDC.put("operation", "EVICT_COMPANY_CACHE");
            String cacheKey = COMPANY_CACHE_PREFIX + companyId;
            redisTemplate.delete(cacheKey);
            log.debug("Company cache evicted: {}", companyId);
        } catch (Exception e) {
            log.warn("Failed to evict company cache {}: {}", companyId, e.getMessage());
        } finally {
            org.slf4j.MDC.clear();
        }
    }

    public void evictPortfolioCache(String portfolioId) {
        try {
            org.slf4j.MDC.put("portfolioId", portfolioId);
            org.slf4j.MDC.put("operation", "EVICT_PORTFOLIO_CACHE");
            String cacheKey = PORTFOLIO_CACHE_PREFIX + portfolioId;
            redisTemplate.delete(cacheKey);
            log.debug("Portfolio cache evicted: {}", portfolioId);
        } catch (Exception e) {
            log.warn("Failed to evict portfolio cache {}: {}", portfolioId, e.getMessage());
        } finally {
            org.slf4j.MDC.clear();
        }
    }

    public void clearAllCache() {
        try {
            org.slf4j.MDC.put("operation", "CLEAR_ALL_CACHE");
            log.info("Clearing all cache");
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("All cache cleared. Total keys deleted: {}", keys.size());
            } else {
                log.debug("Cache is empty, nothing to clear");
            }
        } catch (Exception e) {
            log.error("Error clearing cache: {}", e.getMessage(), e);
        } finally {
            org.slf4j.MDC.clear();
        }
    }

    public void refreshCompanyRanking() {
        try {
            org.slf4j.MDC.put("operation", "REFRESH_RANKING");
            // Здесь можно было бы пересчитать и обновить рейтинг
            // Пока просто продлеваем TTL
            redisTemplate.expire(RANKING_KEY, 24, TimeUnit.HOURS);
            log.debug("Company ranking cache refreshed");
        } catch (Exception e) {
            log.warn("Failed to refresh ranking cache: {}", e.getMessage());
        } finally {
            org.slf4j.MDC.clear();
        }
    }

    @Scheduled(fixedRate = 3600000) // Каждый час
    public void cleanupExpiredCache() {
        try {
            org.slf4j.MDC.put("operation", "CLEANUP_EXPIRED_CACHE");
            // Redis автоматически обрабатывает TTL, но мы можем логировать статистику
            Set<String> allKeys = redisTemplate.keys("*");
            long dbSize = allKeys != null ? allKeys.size() : 0;
            log.info("Current Redis cache size: {} keys", dbSize);
        } catch (Exception e) {
            log.warn("Error checking cache size: {}", e.getMessage());
        } finally {
            org.slf4j.MDC.clear();
        }
    }

    public CacheStats getCacheStats() {
        try {
            org.slf4j.MDC.put("operation", "GET_CACHE_STATS");
            log.debug("Getting cache statistics");
            Set<String> allKeys = redisTemplate.keys("*");
            Set<String> companyKeysSet = redisTemplate.keys(COMPANY_CACHE_PREFIX + "*");
            Set<String> portfolioKeysSet = redisTemplate.keys(PORTFOLIO_CACHE_PREFIX + "*");
            
            Long totalKeys = allKeys != null ? (long) allKeys.size() : 0L;
            Long companyKeys = companyKeysSet != null ? (long) companyKeysSet.size() : 0L;
            Long portfolioKeys = portfolioKeysSet != null ? (long) portfolioKeysSet.size() : 0L;

            CacheStats stats = CacheStats.builder()
                    .totalKeys(totalKeys)
                    .companyKeys(companyKeys)
                    .portfolioKeys(portfolioKeys)
                    .rankingKeys(0L) // Можно вычислить отдельно при необходимости
                    .build();
            
            log.debug("Cache statistics retrieved: total keys {}", totalKeys);
            return stats;
        } catch (Exception e) {
            log.warn("Error getting cache statistics: {}", e.getMessage());
            return CacheStats.builder()
                    .totalKeys(0L)
                    .companyKeys(0L)
                    .portfolioKeys(0L)
                    .rankingKeys(0L)
                    .build();
        } finally {
            org.slf4j.MDC.clear();
        }
    }
}