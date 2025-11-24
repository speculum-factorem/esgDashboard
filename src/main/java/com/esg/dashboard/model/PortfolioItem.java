package com.esg.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItem {
    private String companyId;
    private String companyName;
    private Double investmentAmount;
    private Double weight;
    private ESGRating currentRating;
}

