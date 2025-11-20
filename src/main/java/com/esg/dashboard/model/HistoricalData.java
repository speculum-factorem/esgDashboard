package com.esg.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "historical_data")
public class HistoricalData {
    @Id
    private String id;

    private String companyId;
    private String dataType; // ESG_RATING, CARBON_FOOTPRINT, etc.

    private Map<String, Object> metrics;
    private LocalDateTime recordDate;
    private LocalDateTime createdAt;

    @Builder.Default
    private DataQuality quality = DataQuality.HIGH;

    public enum DataQuality {
        HIGH, MEDIUM, LOW
    }
}