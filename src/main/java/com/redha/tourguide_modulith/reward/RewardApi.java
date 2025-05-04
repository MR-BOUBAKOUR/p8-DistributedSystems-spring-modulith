package com.redha.tourguide_modulith.reward;

import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;

import java.util.UUID;

public interface RewardApi {

    void handleVisitedLocationAdded(VisitedLocationAddedEvent event);

    void calculateRewards(UUID userId);

}
