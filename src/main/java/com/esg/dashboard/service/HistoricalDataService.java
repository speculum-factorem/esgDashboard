package com.esg.dashboard.service;

import com.esg.dashboard.model.HistoricalData;
import com.esg.dashboard.repository.HistoricalDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricalDataService {

    private final MongoTemplate mongoTemplate;
    private final HistoricalDataRepository historicalDataRepository;

    public void saveHistoricalData(String companyId, String dataType, Map<String, Object> metrics) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "SAVE_HISTORICAL_DATA");
            MDC.put("dataType", dataType);
            log.info("Saving historical data for company: {}, type: {}", companyId, dataType);

            HistoricalData historicalData = HistoricalData.builder()
                    .companyId(companyId)
                    .dataType(dataType)
                    .metrics(metrics)
                    .recordDate(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .quality(HistoricalData.DataQuality.HIGH)
                    .build();

            mongoTemplate.save(historicalData);
            log.debug("Historical data successfully saved for company: {}, type: {}", companyId, dataType);
        } catch (Exception e) {
            log.error("Error saving historical data for company {}: {}", companyId, e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    public List<HistoricalData> getHistoricalData(String companyId, String dataType, LocalDateTime from, LocalDateTime to) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_HISTORICAL_DATA");
            MDC.put("dataType", dataType);
            log.info("Fetching historical data for company: {}, type: {}, period: {} - {}", 
                    companyId, dataType, from, to);

            Query query = new Query();
            query.addCriteria(Criteria.where("companyId").is(companyId)
                    .and("dataType").is(dataType)
                    .and("recordDate").gte(from).lte(to));
            query.limit(1000); // Предотвращаем слишком большие наборы данных

            List<HistoricalData> results = mongoTemplate.find(query, HistoricalData.class);
            log.debug("Found {} historical records", results.size());
            return results;
        } finally {
            MDC.clear();
        }
    }

    public List<HistoricalData> getCompanyHistory(String companyId, int limit) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_COMPANY_HISTORY");
            MDC.put("limit", String.valueOf(limit));
            log.info("Fetching company history: {}, limit: {}", companyId, limit);

            Query query = new Query(Criteria.where("companyId").is(companyId));
            query.limit(limit);
            query.with(org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.DESC, "recordDate"));

            List<HistoricalData> results = mongoTemplate.find(query, HistoricalData.class);
            log.debug("Found {} history records for company {}", results.size(), companyId);
            return results;
        } finally {
            MDC.clear();
        }
    }

    public Page<HistoricalData> getCompanyHistory(String companyId, Pageable pageable) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_COMPANY_HISTORY_PAGED");
            MDC.put("page", String.valueOf(pageable.getPageNumber()));
            MDC.put("size", String.valueOf(pageable.getPageSize()));
            log.info("Fetching company history: {} - page: {}, size: {}", companyId, pageable.getPageNumber(), pageable.getPageSize());

            Page<HistoricalData> results = historicalDataRepository.findByCompanyIdOrderByRecordDateDesc(companyId, pageable);
            log.debug("Found {} history records for company {} (page {})", results.getContent().size(), companyId, pageable.getPageNumber());
            return results;
        } finally {
            MDC.clear();
        }
    }

    public Page<HistoricalData> getHistoricalData(String companyId, String dataType, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_HISTORICAL_DATA_PAGED");
            MDC.put("dataType", dataType);
            MDC.put("page", String.valueOf(pageable.getPageNumber()));
            MDC.put("size", String.valueOf(pageable.getPageSize()));
            log.info("Fetching historical data for company: {}, type: {}, period: {} - {} - page: {}, size: {}", 
                    companyId, dataType, from, to, pageable.getPageNumber(), pageable.getPageSize());

            Page<HistoricalData> results = historicalDataRepository.findByCompanyIdAndDataTypeAndDateRange(
                    companyId, dataType, from, to, pageable);
            log.debug("Found {} records for type {} in the specified period (page {})", 
                    results.getContent().size(), dataType, pageable.getPageNumber());
            return results;
        } finally {
            MDC.clear();
        }
    }
}