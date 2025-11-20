package com.esg.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "companies")
public class Company {
    @Id
    private String id;

    @Indexed(unique = true)
    private String companyId;

    private String name;
    private String sector;
    private String industry;

    private ESGRating currentRating;
    private Map<String, Object> additionalMetrics;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}