package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COMPANY_CACHE_PREFIX = "company:";
    private static final String COMPANY_RANKING_KEY = "company:ranking";
    private static final long CACHE_TTL_HOURS = 24;

    public void cacheCompany(Company company) {
        try {
            String cacheKey = COMPANY_CACHE_PREFIX + company.getCompanyId();
            redisTemplate.opsForValue().set(cacheKey, company, CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("Company cached: {}", company.getCompanyId());
        } catch (Exception e) {
            log.error("Failed to cache company {}: {}", company.getCompanyId(), e.getMessage());
        }
    }

    public Company getCachedCompany(String companyId) {
        try {
            String cacheKey = COMPANY_CACHE_PREFIX + companyId;
            return (Company) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.error("Failed to get cached company {}: {}", companyId, e.getMessage());
            return null;
        }
    }

    public void evictCompanyCache(String companyId) {
        try {
            String cacheKey = COMPANY_CACHE_PREFIX + companyId;
            redisTemplate.delete(cacheKey);
            log.debug("Company cache evicted: {}", companyId);
        } catch (Exception e) {
            log.error("Failed to evict company cache {}: {}", companyId, e.getMessage());
        }
    }

    public void updateCompanyRanking(String companyId, Double score) {
        try {
            redisTemplate.opsForZSet().add(COMPANY_RANKING_KEY, companyId, score);
            log.debug("Company ranking updated: {} with score {}", companyId, score);
        } catch (Exception e) {
            log.error("Failed to update company ranking {}: {}", companyId, e.getMessage());
        }
    }
}