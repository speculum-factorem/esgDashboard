package com.esg.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "portfolios")
public class Portfolio {
    @Id
    private String id;

    private String portfolioId;
    private String portfolioName;
    private String clientId;
    private String clientName;

    private List<PortfolioItem> items;
    private PortfolioAggregate aggregateScores;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}