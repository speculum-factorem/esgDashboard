package com.esg.dashboard.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheStats {
    private Long totalKeys;
    private Long companyKeys;
    private Long portfolioKeys;
    private Long rankingKeys;
}

