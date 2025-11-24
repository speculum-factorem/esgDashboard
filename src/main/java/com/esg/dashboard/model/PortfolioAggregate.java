package com.esg.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAggregate {
    private Double totalEsgScore;
    private Double carbonFootprint;
    private Double socialImpactScore;
    private String averageRating;
    private Integer totalCompanies;
    private Double totalInvestment;
}

