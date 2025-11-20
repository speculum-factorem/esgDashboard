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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PortfolioItem {
    private String companyId;
    private String companyName;
    private Double investmentAmount;
    private Double weight;
    private ESGRating currentRating;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PortfolioAggregate {
    private Double totalEsgScore;
    private Double carbonFootprint;
    private Double socialImpactScore;
    private String averageRating;
    private Integer totalCompanies;
    private Double totalInvestment;
}