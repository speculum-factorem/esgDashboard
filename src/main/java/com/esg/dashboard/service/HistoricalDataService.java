package com.esg.dashboard.service;

import com.esg.dashboard.model.HistoricalData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void saveHistoricalData(String companyId, String dataType, Map<String, Object> metrics) {
        try {
            HistoricalData historicalData = HistoricalData.builder()
                    .companyId(companyId)
                    .dataType(dataType)
                    .metrics(metrics)
                    .recordDate(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .quality(HistoricalData.DataQuality.HIGH)
                    .build();

            mongoTemplate.save(historicalData);
            log.debug("Historical data saved for company: {}, type: {}", companyId, dataType);
        } catch (Exception e) {
            log.error("Error saving historical data for company {}: {}", companyId, e.getMessage());
        }
    }

    public List<HistoricalData> getHistoricalData(String companyId, String dataType, LocalDateTime from, LocalDateTime to) {
        Query query = new Query();
        query.addCriteria(Criteria.where("companyId").is(companyId)
                .and("dataType").is(dataType)
                .and("recordDate").gte(from).lte(to));
        query.limit(1000); // Prevent too large datasets

        return mongoTemplate.find(query, HistoricalData.class);
    }

    public List<HistoricalData> getCompanyHistory(String companyId, int limit) {
        Query query = new Query(Criteria.where("companyId").is(companyId));
        query.limit(limit);
        query.with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "recordDate"));

        return mongoTemplate.find(query, HistoricalData.class);
    }
}