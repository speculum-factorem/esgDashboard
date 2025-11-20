package com.esg.dashboard.service;

import com.esg.dashboard.model.ESGUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final MongoTemplate mongoTemplate;

    public void saveEvent(ESGUpdateEvent event) {
        try {
            event.setTimestamp(LocalDateTime.now());
            mongoTemplate.save(event, "esg_events");
            log.debug("ESG event saved: {} for company {}", event.getEventType(), event.getCompanyId());
        } catch (Exception e) {
            log.error("Failed to save ESG event: {}", e.getMessage());
        }
    }

    public List<ESGUpdateEvent> getRecentEvents(int limit) {
        var sort = org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "timestamp"
        );
        var query = new org.springframework.data.mongodb.core.query.Query()
                .with(sort)
                .limit(limit);

        return mongoTemplate.find(query, ESGUpdateEvent.class, "esg_events");
    }

    public List<ESGUpdateEvent> getCompanyEvents(String companyId, int limit) {
        var sort = org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "timestamp"
        );
        var query = new org.springframework.data.mongodb.core.query.Query()
                .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("companyId").is(companyId))
                .with(sort)
                .limit(limit);

        return mongoTemplate.find(query, ESGUpdateEvent.class, "esg_events");
    }
}