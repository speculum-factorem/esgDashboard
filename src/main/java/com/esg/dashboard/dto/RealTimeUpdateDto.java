package com.esg.dashboard.dto;

import com.esg.dashboard.model.ESGRating;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeUpdateDto {
    private String type;
    private String companyId;
    private String companyName;
    private ESGRating rating;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String message;

    public enum UpdateType {
        COMPANY_UPDATE,
        RATING_UPDATE,
        PORTFOLIO_UPDATE,
        RANKING_CHANGE
    }
}