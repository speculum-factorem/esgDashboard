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
public class ESGUpdateEvent {
    private String eventId;
    private String companyId;
    private String companyName;
    private ESGRating previousRating;
    private ESGRating newRating;
    private EventType eventType;
    private LocalDateTime timestamp;
    private String triggeredBy;

    public enum EventType {
        RATING_UPDATE,
        RANKING_CHANGE,
        PORTFOLIO_UPDATE,
        MANUAL_UPDATE
    }
}