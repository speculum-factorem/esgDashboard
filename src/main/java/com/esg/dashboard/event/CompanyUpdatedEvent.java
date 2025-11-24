package com.esg.dashboard.event;

import com.esg.dashboard.model.Company;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CompanyUpdatedEvent extends ApplicationEvent {
    private final Company company;
    private final String action; // CREATE, UPDATE, DELETE

    public CompanyUpdatedEvent(Object source, Company company, String action) {
        super(source);
        this.company = company;
        this.action = action;
    }
}

