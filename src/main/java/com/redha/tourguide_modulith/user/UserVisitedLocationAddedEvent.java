package com.redha.tourguide_modulith.user;

import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserVisitedLocationAddedEvent extends ApplicationEvent {

    private final UUID userId;
    private final VisitedLocationDto visitedLocation;

    public UserVisitedLocationAddedEvent(Object source, UUID userId, VisitedLocationDto visitedLocation) {
        super(source);
        this.userId = userId;
        this.visitedLocation = visitedLocation;
    }
}
