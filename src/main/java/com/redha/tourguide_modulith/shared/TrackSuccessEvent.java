package com.redha.tourguide_modulith.shared;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class TrackSuccessEvent extends ApplicationEvent {

    private final UUID userId;
    private final VisitedLocationDto visitedLocation;

    public TrackSuccessEvent(Object source, UUID userId, VisitedLocationDto visitedLocation) {
        super(source);
        this.userId = userId;
        this.visitedLocation = visitedLocation;
    }

    @Override
    public String toString() {
        return "TrackSuccessEvent{" +
                "userId=" + userId +
                ", visitedLocation=" + visitedLocation +
                ", source=" + getSource() +
                '}';
    }

}
