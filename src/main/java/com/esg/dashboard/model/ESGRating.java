package com.esg.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ESGRating {
    private Double overallScore;
    private Double environmentalScore;
    private Double socialScore;
    private Double governanceScore;
    private Double carbonFootprint; // tCO2e
    private Double socialImpactScore;
    private String ratingGrade; // AAA, AA, A, etc.
    private LocalDateTime calculationDate;

    @Builder.Default
    private Integer ranking = 0;
}