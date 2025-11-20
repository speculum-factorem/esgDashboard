package com.esg.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private String id;

    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Sector is required")
    private String sector;

    private String industry;

    @NotNull(message = "ESG rating is required")
    private ESGRatingDto currentRating;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ESGRatingDto {
    @NotNull(message = "Overall score is required")
    @PositiveOrZero(message = "Score must be positive or zero")
    private Double overallScore;

    @NotNull(message = "Environmental score is required")
    @PositiveOrZero(message = "Score must be positive or zero")
    private Double environmentalScore;

    @NotNull(message = "Social score is required")
    @PositiveOrZero(message = "Score must be positive or zero")
    private Double socialScore;

    @NotNull(message = "Governance score is required")
    @PositiveOrZero(message = "Score must be positive or zero")
    private Double governanceScore;

    @NotNull(message = "Carbon footprint is required")
    @PositiveOrZero(message = "Carbon footprint must be positive or zero")
    private Double carbonFootprint;

    @NotNull(message = "Social impact score is required")
    @PositiveOrZero(message = "Social impact score must be positive or zero")
    private Double socialImpactScore;

    private String ratingGrade;
    private Integer ranking;
}