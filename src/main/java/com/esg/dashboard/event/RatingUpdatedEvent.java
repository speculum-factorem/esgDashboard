package com.esg.dashboard.event;

import com.esg.dashboard.model.ESGRating;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RatingUpdatedEvent extends ApplicationEvent {
    private final String companyId;
    private final ESGRating previousRating;
    private final ESGRating newRating;

    public RatingUpdatedEvent(Object source, String companyId, ESGRating previousRating, ESGRating newRating) {
        super(source);
        this.companyId = companyId;
        this.previousRating = previousRating;
        this.newRating = newRating;
    }
}

