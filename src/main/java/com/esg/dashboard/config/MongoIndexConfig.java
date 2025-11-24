package com.esg.dashboard.config;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.HistoricalData;
import com.esg.dashboard.model.Portfolio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import jakarta.annotation.PostConstruct;

/**
 * Конфигурация индексов MongoDB для оптимизации запросов
 * Создает индексы для часто используемых полей
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void createIndexes() {
        try {
            log.info("Creating MongoDB indexes for optimization");

            // Индексы для Company
            IndexOperations companyIndexOps = mongoTemplate.indexOps(Company.class);
            companyIndexOps.ensureIndex(new Index().on("companyId", org.springframework.data.domain.Sort.Direction.ASC).unique());
            companyIndexOps.ensureIndex(new Index().on("sector", org.springframework.data.domain.Sort.Direction.ASC));
            companyIndexOps.ensureIndex(new Index().on("currentRating.overallScore", org.springframework.data.domain.Sort.Direction.DESC));
            companyIndexOps.ensureIndex(new Index().on("currentRating.ranking", org.springframework.data.domain.Sort.Direction.ASC));
            companyIndexOps.ensureIndex(new Index().on("updatedAt", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("Company indexes created");

            // Индексы для Portfolio
            IndexOperations portfolioIndexOps = mongoTemplate.indexOps(Portfolio.class);
            portfolioIndexOps.ensureIndex(new Index().on("portfolioId", org.springframework.data.domain.Sort.Direction.ASC).unique());
            portfolioIndexOps.ensureIndex(new Index().on("clientId", org.springframework.data.domain.Sort.Direction.ASC));
            portfolioIndexOps.ensureIndex(new Index().on("clientId", org.springframework.data.domain.Sort.Direction.ASC)
                    .on("portfolioName", org.springframework.data.domain.Sort.Direction.ASC));
            portfolioIndexOps.ensureIndex(new Index().on("updatedAt", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("Portfolio indexes created");

            // Индексы для HistoricalData
            IndexOperations historicalDataIndexOps = mongoTemplate.indexOps(HistoricalData.class);
            historicalDataIndexOps.ensureIndex(new Index().on("companyId", org.springframework.data.domain.Sort.Direction.ASC)
                    .on("dataType", org.springframework.data.domain.Sort.Direction.ASC)
                    .on("recordDate", org.springframework.data.domain.Sort.Direction.DESC));
            historicalDataIndexOps.ensureIndex(new Index().on("companyId", org.springframework.data.domain.Sort.Direction.ASC)
                    .on("recordDate", org.springframework.data.domain.Sort.Direction.DESC));
            historicalDataIndexOps.ensureIndex(new Index().on("recordDate", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("HistoricalData indexes created");

            log.info("MongoDB indexes created successfully");
        } catch (Exception e) {
            log.error("Error creating MongoDB indexes: {}", e.getMessage(), e);
        }
    }
}

