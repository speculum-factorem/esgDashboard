package com.esg.dashboard.service;

import com.esg.dashboard.event.EventPublisher;
import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;
    private final CompanyCacheService companyCacheService;
    private final EventPublisher eventPublisher;

    private static final String ESG_RANKING_KEY = "esg:ranking";

    @Transactional
    public Company saveOrUpdateCompany(Company company) {
        Assert.notNull(company, "Company cannot be null");

        try {
            MDC.put("companyId", company.getCompanyId());
            log.info("Saving or updating company: {}", company.getName());

            boolean isNew = company.getId() == null;
            MDC.put("isNew", String.valueOf(isNew));
            company.setUpdatedAt(LocalDateTime.now());
            Company savedCompany = companyRepository.save(company);

            // Обновляем кэш через специализированный сервис
            companyCacheService.cacheCompany(savedCompany);
            updateRanking(savedCompany);

            // Публикуем событие для других компонентов
            String action = isNew ? "CREATE" : "UPDATE";
            eventPublisher.publishCompanyUpdated(savedCompany, action);

            log.info("Company successfully saved with ID: {}", savedCompany.getId());
            return savedCompany;
        } finally {
            MDC.clear();
        }
    }

    public Optional<Company> findByCompanyId(String companyId) {
        Assert.hasText(companyId, "Company ID cannot be empty");

        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "FIND_BY_ID");
            log.debug("Looking up company by ID: {}", companyId);

            // Сначала проверяем кэш через специализированный сервис
            Company cachedCompany = companyCacheService.getCachedCompany(companyId);
            if (cachedCompany != null) {
                log.debug("Company found in cache");
                return Optional.of(cachedCompany);
            }

            // Если не найдено в кэше, обращаемся к базе данных
            Optional<Company> company = companyRepository.findByCompanyId(companyId);
            company.ifPresent(companyCacheService::cacheCompany);

            return company;
        } finally {
            MDC.clear();
        }
    }

    public List<Company> getTopRankedCompanies(int limit) {
        Assert.isTrue(limit > 0, "Limit must be positive");
        Assert.isTrue(limit <= 1000, "Limit cannot exceed 1000");

        MDC.put("operation", "GET_TOP_RANKED");
        MDC.put("limit", String.valueOf(limit));
        log.info("Fetching top {} ranked companies", limit);

        // Сначала пробуем получить из Redis Sorted Set
        Set<Object> topCompanyIds = zSetOperations.reverseRange(ESG_RANKING_KEY, 0, limit - 1);
        if (topCompanyIds != null && !topCompanyIds.isEmpty()) {
            log.debug("Found {} companies in ranking cache", topCompanyIds.size());
            return topCompanyIds.stream()
                    .map(id -> findByCompanyId((String) id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        // Если не найдено в кэше, обращаемся к базе данных
        log.debug("Ranking not found in cache, querying database");
        return companyRepository.findTopRankedCompanies(limit);
    }

    public org.springframework.data.domain.Page<Company> getTopRankedCompanies(org.springframework.data.domain.Pageable pageable) {
        MDC.put("operation", "GET_TOP_RANKED_PAGED");
        MDC.put("page", String.valueOf(pageable.getPageNumber()));
        MDC.put("size", String.valueOf(pageable.getPageSize()));
        log.info("Fetching top ranked companies - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        // Используем пагинацию из репозитория
        return companyRepository.findTopRankedCompanies(pageable);
    }

    public List<Company> getCompaniesBySector(String sector) {
        Assert.hasText(sector, "Sector cannot be empty");

        try {
            MDC.put("sector", sector);
            log.info("Fetching companies in sector: {}", sector);

            List<Company> companies = companyRepository.findBySector(sector);
            log.debug("Found {} companies in sector: {}", companies.size(), sector);
            return companies;
        } finally {
            MDC.clear();
        }
    }

    public org.springframework.data.domain.Page<Company> getCompaniesBySector(String sector, org.springframework.data.domain.Pageable pageable) {
        Assert.hasText(sector, "Sector cannot be empty");

        try {
            MDC.put("sector", sector);
            MDC.put("page", String.valueOf(pageable.getPageNumber()));
            MDC.put("size", String.valueOf(pageable.getPageSize()));
            log.info("Fetching companies in sector: {} - page: {}, size: {}", sector, pageable.getPageNumber(), pageable.getPageSize());

            org.springframework.data.domain.Page<Company> companies = companyRepository.findBySector(sector, pageable);
            log.debug("Found {} companies in sector {} (page {})", companies.getContent().size(), sector, pageable.getPageNumber());
            return companies;
        } finally {
            MDC.clear();
        }
    }

    public java.util.Map<String, Company> batchLoadCompanies(List<String> companyIds) {
        Assert.notNull(companyIds, "Company IDs list cannot be null");
        Assert.notEmpty(companyIds, "Company IDs list cannot be empty");

        MDC.put("operation", "BATCH_LOAD");
        MDC.put("count", String.valueOf(companyIds.size()));
        log.debug("Batch loading {} companies", companyIds.size());

        // Сначала пытаемся получить из кэша
        java.util.Map<String, Company> result = new java.util.HashMap<>();
        java.util.List<String> missingFromCache = new java.util.ArrayList<>();

        for (String companyId : companyIds) {
            Company cached = companyCacheService.getCachedCompany(companyId);
            if (cached != null) {
                result.put(companyId, cached);
            } else {
                missingFromCache.add(companyId);
            }
        }

        // Загружаем отсутствующие компании из базы данных
        if (!missingFromCache.isEmpty()) {
            log.debug("Loading {} companies from database", missingFromCache.size());
            List<Company> companiesFromDb = companyRepository.findByCompanyIdIn(missingFromCache);
            for (Company company : companiesFromDb) {
                result.put(company.getCompanyId(), company);
                companyCacheService.cacheCompany(company);
            }
        }

        log.debug("Batch loaded {} companies", result.size());
        return result;
    }

    @Transactional
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

            ESGRating previousRating = company.getCurrentRating();
            company.setCurrentRating(newRating);
            company.setUpdatedAt(LocalDateTime.now());

            Company updatedCompany = companyRepository.save(company);

            // Update cache and ranking using dedicated services
            companyCacheService.cacheCompany(updatedCompany);
            updateRanking(updatedCompany);

            // Publish event for other components
            eventPublisher.publishRatingUpdated(companyId, previousRating, newRating);

            log.info("ESG rating updated successfully for company: {}", companyId);
        } finally {
            MDC.clear();
        }
    }

    private void updateRanking(Company company) {
        try {
            if (company.getCurrentRating() == null || company.getCurrentRating().getOverallScore() == null) {
                log.warn("Cannot update ranking for company {}: rating or score is null", company.getCompanyId());
                return;
            }
            Double score = company.getCurrentRating().getOverallScore();
            zSetOperations.add(ESG_RANKING_KEY, company.getCompanyId(), score);
            // Также обновляем через сервис кэша для консистентности
            companyCacheService.updateCompanyRanking(company.getCompanyId(), score);
            log.debug("Updated ranking for company: {} with score: {}", company.getCompanyId(), score);
        } catch (Exception e) {
            log.error("Failed to update ranking for company {}: {}", company.getCompanyId(), e.getMessage(), e);
            // Don't throw exception to avoid breaking the main flow, but log it properly
        }
    }
}