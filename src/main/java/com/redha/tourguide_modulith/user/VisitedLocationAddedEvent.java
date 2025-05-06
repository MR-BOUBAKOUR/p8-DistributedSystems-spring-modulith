package com.redha.tourguide_modulith.user;

import com.redha.tourguide_modulith.domain.VisitedLocationDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class VisitedLocationAddedEvent extends ApplicationEvent {

    private final UUID userId;
    private final VisitedLocationDto visitedLocation;

    public VisitedLocationAddedEvent(Object source, UUID userId, VisitedLocationDto visitedLocation) {
        super(source);
        this.userId = userId;
        this.visitedLocation = visitedLocation;
    }
}
