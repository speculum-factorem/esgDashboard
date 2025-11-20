package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;

    private static final String COMPANY_CACHE_PREFIX = "company:";
    private static final String ESG_RANKING_KEY = "esg:ranking";
    private static final long CACHE_TTL = 30; // minutes

    public Company saveOrUpdateCompany(Company company) {
        Assert.notNull(company, "Company cannot be null");

        try {
            MDC.put("companyId", company.getCompanyId());
            log.info("Saving or updating company: {}", company.getName());

            company.setUpdatedAt(LocalDateTime.now());
            Company savedCompany = companyRepository.save(company);

            // Update cache
            cacheCompany(savedCompany);
            updateRanking(savedCompany);

            log.info("Company saved successfully with ID: {}", savedCompany.getId());
            return savedCompany;
        } finally {
            MDC.clear();
        }
    }

    public Optional<Company> findByCompanyId(String companyId) {
        Assert.hasText(companyId, "Company ID cannot be empty");

        try {
            MDC.put("companyId", companyId);
            log.debug("Looking up company by ID: {}", companyId);

            // Try cache first
            Company cachedCompany = getCachedCompany(companyId);
            if (cachedCompany != null) {
                log.debug("Company found in cache");
                return Optional.of(cachedCompany);
            }

            // Fallback to database
            Optional<Company> company = companyRepository.findByCompanyId(companyId);
            company.ifPresent(this::cacheCompany);

            return company;
        } finally {
            MDC.clear();
        }
    }

    public List<Company> getTopRankedCompanies(int limit) {
        Assert.isTrue(limit > 0, "Limit must be positive");

        log.info("Fetching top {} ranked companies", limit);

        // Try Redis Sorted Set first
        Set<Object> topCompanyIds = zSetOperations.reverseRange(ESG_RANKING_KEY, 0, limit - 1);
        if (topCompanyIds != null && !topCompanyIds.isEmpty()) {
            log.debug("Found {} companies in ranking cache", topCompanyIds.size());
            return topCompanyIds.stream()
                    .map(id -> findByCompanyId((String) id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        // Fallback to database
        return companyRepository.findTopRankedCompanies(limit);
    }

    public void updateESGRating(String companyId, ESGRating newRating) {
        Assert.hasText(companyId, "Company ID cannot be empty");
        Assert.notNull(newRating, "ESG Rating cannot be null");

        try {
            MDC.put("companyId", companyId);
            log.info("Updating ESG rating for company: {}", companyId);

            Company company = companyRepository.findByCompanyId(companyId)
                    .orElseThrow(() -> {
                        log.error("Company not found: {}", companyId);
                        return new IllegalArgumentException("Company not found: " + companyId);
                    });

            company.setCurrentRating(newRating);
            company.setUpdatedAt(LocalDateTime.now());

            Company updatedCompany = companyRepository.save(company);

            // Update cache and ranking
            cacheCompany(updatedCompany);
            updateRanking(updatedCompany);

            log.info("ESG rating updated successfully for company: {}", companyId);
        } finally {
            MDC.clear();
        }
    }

    private void cacheCompany(Company company) {
        try {
            String cacheKey = COMPANY_CACHE_PREFIX + company.getCompanyId();
            redisTemplate.opsForValue().set(cacheKey, company, CACHE_TTL, TimeUnit.MINUTES);
            log.debug("Company cached with key: {}", cacheKey);
        } catch (Exception e) {
            log.warn("Failed to cache company: {}", e.getMessage());
        }
    }

    private Company getCachedCompany(String companyId) {
        try {
            String cacheKey = COMPANY_CACHE_PREFIX + companyId;
            return (Company) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("Failed to get company from cache: {}", e.getMessage());
            return null;
        }
    }

    private void updateRanking(Company company) {
        try {
            Double score = company.getCurrentRating().getOverallScore();
            zSetOperations.add(ESG_RANKING_KEY, company.getCompanyId(), score);
            log.debug("Updated ranking for company: {} with score: {}", company.getCompanyId(), score);
        } catch (Exception e) {
            log.warn("Failed to update ranking: {}", e.getMessage());
        }
    }
}