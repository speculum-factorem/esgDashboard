package com.esg.dashboard.service;

import com.esg.dashboard.model.ESGUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
            MDC.put("companyId", event.getCompanyId());
            MDC.put("operation", "SAVE_EVENT");
            MDC.put("eventType", event.getEventType() != null ? event.getEventType().toString() : "");
            log.info("Saving ESG event: {} for company {}", event.getEventType(), event.getCompanyId());

            event.setTimestamp(LocalDateTime.now());
            mongoTemplate.save(event, "esg_events");
            log.debug("ESG event successfully saved");
        } catch (Exception e) {
            log.error("Failed to save ESG event: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    public List<ESGUpdateEvent> getRecentEvents(int limit) {
        try {
            MDC.put("operation", "GET_RECENT_EVENTS");
            MDC.put("limit", String.valueOf(limit));
            log.info("Fetching recent events, limit: {}", limit);

            var sort = org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.DESC, "timestamp"
            );
            var query = new org.springframework.data.mongodb.core.query.Query()
                    .with(sort)
                    .limit(limit);

            List<ESGUpdateEvent> events = mongoTemplate.find(query, ESGUpdateEvent.class, "esg_events");
            log.debug("Found {} recent events", events.size());
            return events;
        } finally {
            MDC.clear();
        }
    }

    public org.springframework.data.domain.Page<ESGUpdateEvent> getRecentEvents(org.springframework.data.domain.Pageable pageable) {
        try {
            MDC.put("operation", "GET_RECENT_EVENTS_PAGED");
            MDC.put("page", String.valueOf(pageable.getPageNumber()));
            MDC.put("size", String.valueOf(pageable.getPageSize()));
            log.info("Fetching recent events - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

            var sort = org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.DESC, "timestamp"
            );
            var query = new org.springframework.data.mongodb.core.query.Query()
                    .with(sort)
                    .with(pageable);

            long total = mongoTemplate.count(new org.springframework.data.mongodb.core.query.Query(), ESGUpdateEvent.class, "esg_events");
            List<ESGUpdateEvent> events = mongoTemplate.find(query, ESGUpdateEvent.class, "esg_events");
            
            org.springframework.data.domain.Page<ESGUpdateEvent> page = new org.springframework.data.domain.PageImpl<>(
                    events, pageable, total);
            log.debug("Found {} recent events (page {})", events.size(), pageable.getPageNumber());
            return page;
        } finally {
            MDC.clear();
        }
    }

    public List<ESGUpdateEvent> getCompanyEvents(String companyId, int limit) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_COMPANY_EVENTS");
            MDC.put("limit", String.valueOf(limit));
            log.info("Fetching events for company: {}, limit: {}", companyId, limit);

            var sort = org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.DESC, "timestamp"
            );
            var query = new org.springframework.data.mongodb.core.query.Query()
                    .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("companyId").is(companyId))
                    .with(sort)
                    .limit(limit);

            List<ESGUpdateEvent> events = mongoTemplate.find(query, ESGUpdateEvent.class, "esg_events");
            log.debug("Found {} events for company {}", events.size(), companyId);
            return events;
        } finally {
            MDC.clear();
        }
    }

    public org.springframework.data.domain.Page<ESGUpdateEvent> getCompanyEvents(String companyId, org.springframework.data.domain.Pageable pageable) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_COMPANY_EVENTS_PAGED");
            MDC.put("page", String.valueOf(pageable.getPageNumber()));
            MDC.put("size", String.valueOf(pageable.getPageSize()));
            log.info("Fetching events for company: {} - page: {}, size: {}", companyId, pageable.getPageNumber(), pageable.getPageSize());

            var sort = org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.DESC, "timestamp"
            );
            var query = new org.springframework.data.mongodb.core.query.Query()
                    .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("companyId").is(companyId))
                    .with(sort)
                    .with(pageable);

            var countQuery = new org.springframework.data.mongodb.core.query.Query()
                    .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("companyId").is(companyId));
            long total = mongoTemplate.count(countQuery, ESGUpdateEvent.class, "esg_events");
            List<ESGUpdateEvent> events = mongoTemplate.find(query, ESGUpdateEvent.class, "esg_events");
            
            org.springframework.data.domain.Page<ESGUpdateEvent> page = new org.springframework.data.domain.PageImpl<>(
                    events, pageable, total);
            log.debug("Found {} events for company {} (page {})", events.size(), companyId, pageable.getPageNumber());
            return page;
        } finally {
            MDC.clear();
        }
    }
}