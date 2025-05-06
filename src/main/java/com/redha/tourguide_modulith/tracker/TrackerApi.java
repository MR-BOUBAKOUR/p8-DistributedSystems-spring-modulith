package com.redha.tourguide_modulith.tracker;

import com.redha.tourguide_modulith.shared.UsersInitializedEvent;
import org.springframework.context.event.EventListener;

public interface TrackerApi {

    @EventListener(UsersInitializedEvent.class)
    void startTracking();

    void stopTracking();

}

