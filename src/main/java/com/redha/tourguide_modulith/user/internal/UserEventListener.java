package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.shared.TrackSuccessEvent;
import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Handles the TrackSuccessEvent triggered after a user's location has been successfully tracked.
     * ➤ Origin: Emitted by LocationService.trackUserLocation(UUID userId) after GPS data is processed.
     * ➤ Adds the visited location to the user's history.
     * ➤ Then emits a VisitedLocationAddedEvent to trigger downstream actions -> reward calculation.
     */
    @EventListener
    public void handleTrackSuccess(TrackSuccessEvent event) {

        userService.addVisitedLocation(event.getUserId(), event.getVisitedLocation());

        eventPublisher.publishEvent(new VisitedLocationAddedEvent(this, event.getUserId(), event.getVisitedLocation()));

    }
}

