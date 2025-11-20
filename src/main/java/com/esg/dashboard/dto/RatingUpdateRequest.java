package com.esg.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingUpdateRequest {

    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotNull(message = "ESG rating is required")
    private ESGRatingDto rating;

    private String updateReason;
    private String updatedBy;
}