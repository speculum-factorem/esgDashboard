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
            String cacheKey = COMPANY_CACHE_PREFIX + companyId;
            redisTemplate.delete(cacheKey);
            log.debug("Evicted company cache for: {}", companyId);
        } catch (Exception e) {
            log.warn("Failed to evict company cache for {}: {}", companyId, e.getMessage());
        }
    }

    public void evictPortfolioCache(String portfolioId) {
        try {
            String cacheKey = PORTFOLIO_CACHE_PREFIX + portfolioId;
            redisTemplate.delete(cacheKey);
            log.debug("Evicted portfolio cache for: {}", portfolioId);
        } catch (Exception e) {
            log.warn("Failed to evict portfolio cache for {}: {}", portfolioId, e.getMessage());
        }
    }

    public void clearAllCache() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared all cache entries. Total keys deleted: {}", keys.size());
            }
        } catch (Exception e) {
            log.error("Failed to clear cache: {}", e.getMessage());
        }
    }

    public void refreshCompanyRanking() {
        try {
            // This would recalculate and update the ranking
            // For now, just extend TTL
            redisTemplate.expire(RANKING_KEY, 24, TimeUnit.HOURS);
            log.debug("Refreshed company ranking cache");
        } catch (Exception e) {
            log.warn("Failed to refresh ranking cache: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredCache() {
        try {
            // Redis handles TTL automatically, but we can log stats
            Long dbSize = redisTemplate.getConnectionFactory().getConnection().dbSize();
            log.info("Current Redis cache size: {} keys", dbSize);
        } catch (Exception e) {
            log.warn("Failed to check cache size: {}", e.getMessage());
        }
    }

    public CacheStats getCacheStats() {
        try {
            Long totalKeys = redisTemplate.getConnectionFactory().getConnection().dbSize();
            Long companyKeys = redisTemplate.keys(COMPANY_CACHE_PREFIX + "*").size();
            Long portfolioKeys = redisTemplate.keys(PORTFOLIO_CACHE_PREFIX + "*").size();

            return CacheStats.builder()
                    .totalKeys(totalKeys)
                    .companyKeys(companyKeys)
                    .portfolioKeys(portfolioKeys)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to get cache stats: {}", e.getMessage());
            return CacheStats.builder().build();
        }
    }
}

@Data
@Builder
class CacheStats {
    private Long totalKeys;
    private Long companyKeys;
    private Long portfolioKeys;
    private Long rankingKeys;
}