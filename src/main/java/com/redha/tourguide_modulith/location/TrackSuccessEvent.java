package com.redha.tourguide_modulith.location;

import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class TrackSuccessEvent extends ApplicationEvent {
    private final UUID userId;
    private final VisitedLocation visitedLocation;

    public TrackSuccessEvent(Object source, UUID userId, VisitedLocation visitedLocation) {
        super(source);
        this.userId = userId;
        this.visitedLocation = visitedLocation;
    }
}
