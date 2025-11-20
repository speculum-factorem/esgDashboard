package com.esg.dashboard.service;

import com.esg.dashboard.model.ESGUpdateEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private EventService eventService;

    @Test
    void saveEvent_ShouldSaveToMongoDB() {
        // Arrange
        ESGUpdateEvent event = ESGUpdateEvent.builder()
                .eventId("EVENT001")
                .companyId("COMP001")
                .companyName("Test Company")
                .eventType(ESGUpdateEvent.EventType.RATING_UPDATE)
                .build();

        // Act
        eventService.saveEvent(event);

        // Assert
        verify(mongoTemplate).save(any(ESGUpdateEvent.class), eq("esg_events"));
    }
}