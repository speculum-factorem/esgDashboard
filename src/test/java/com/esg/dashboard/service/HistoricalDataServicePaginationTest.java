package com.esg.dashboard.service;

import com.esg.dashboard.model.HistoricalData;
import com.esg.dashboard.repository.HistoricalDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricalDataServicePaginationTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private HistoricalDataRepository historicalDataRepository;

    private HistoricalDataService historicalDataService;

    @BeforeEach
    void setUp() {
        historicalDataService = new HistoricalDataService(mongoTemplate, historicalDataRepository);
    }

    @Test
    void testGetCompanyHistoryWithPagination() {
        String companyId = "COMP001";
        Pageable pageable = PageRequest.of(0, 20);
        List<HistoricalData> history = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            HistoricalData data = HistoricalData.builder()
                    .companyId(companyId)
                    .dataType("rating")
                    .recordDate(LocalDateTime.now().minusDays(i))
                    .build();
            history.add(data);
        }
        Page<HistoricalData> page = new org.springframework.data.domain.PageImpl<>(history, pageable, 100);

        when(historicalDataRepository.findByCompanyIdOrderByRecordDateDesc(companyId, pageable)).thenReturn(page);

        Page<HistoricalData> result = historicalDataService.getCompanyHistory(companyId, pageable);

        assertNotNull(result);
        assertEquals(20, result.getContent().size());
        assertEquals(100, result.getTotalElements());
        verify(historicalDataRepository).findByCompanyIdOrderByRecordDateDesc(companyId, pageable);
    }

    @Test
    void testGetHistoricalDataByTypeWithPagination() {
        String companyId = "COMP001";
        String dataType = "rating";
        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        LocalDateTime to = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 15);
        List<HistoricalData> history = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            HistoricalData data = HistoricalData.builder()
                    .companyId(companyId)
                    .dataType(dataType)
                    .recordDate(from.plusDays(i))
                    .build();
            history.add(data);
        }
        Page<HistoricalData> page = new org.springframework.data.domain.PageImpl<>(history, pageable, 30);

        when(historicalDataRepository.findByCompanyIdAndDataTypeAndDateRange(
                companyId, dataType, from, to, pageable)).thenReturn(page);

        Page<HistoricalData> result = historicalDataService.getHistoricalData(
                companyId, dataType, from, to, pageable);

        assertNotNull(result);
        assertEquals(15, result.getContent().size());
        assertEquals(30, result.getTotalElements());
        verify(historicalDataRepository).findByCompanyIdAndDataTypeAndDateRange(
                companyId, dataType, from, to, pageable);
    }
}

