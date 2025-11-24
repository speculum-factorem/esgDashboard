package com.esg.dashboard.service;

import com.esg.dashboard.model.ESGUpdateEvent;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServicePaginationTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(mongoTemplate);
    }

    @Test
    void testGetRecentEventsWithPagination() {
        Pageable pageable = PageRequest.of(0, 20);
        List<ESGUpdateEvent> events = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ESGUpdateEvent event = new ESGUpdateEvent();
            event.setCompanyId("COMP" + i);
            event.setTimestamp(LocalDateTime.now().minusHours(i));
            events.add(event);
        }

        when(mongoTemplate.find(any(), eq(ESGUpdateEvent.class), eq("esg_events"))).thenReturn(events);
        when(mongoTemplate.count(any(), eq(ESGUpdateEvent.class), eq("esg_events"))).thenReturn(100L);

        Page<ESGUpdateEvent> result = eventService.getRecentEvents(pageable);

        assertNotNull(result);
        assertEquals(20, result.getContent().size());
        assertEquals(100, result.getTotalElements());
        verify(mongoTemplate).find(any(), eq(ESGUpdateEvent.class), eq("esg_events"));
    }

    @Test
    void testGetCompanyEventsWithPagination() {
        String companyId = "COMP001";
        Pageable pageable = PageRequest.of(0, 10);
        List<ESGUpdateEvent> events = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ESGUpdateEvent event = new ESGUpdateEvent();
            event.setCompanyId(companyId);
            event.setTimestamp(LocalDateTime.now().minusHours(i));
            events.add(event);
        }

        when(mongoTemplate.find(any(), eq(ESGUpdateEvent.class), eq("esg_events"))).thenReturn(events);
        when(mongoTemplate.count(any(), eq(ESGUpdateEvent.class), eq("esg_events"))).thenReturn(50L);

        Page<ESGUpdateEvent> result = eventService.getCompanyEvents(companyId, pageable);

        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(50, result.getTotalElements());
        verify(mongoTemplate).find(any(), eq(ESGUpdateEvent.class), eq("esg_events"));
    }
}

