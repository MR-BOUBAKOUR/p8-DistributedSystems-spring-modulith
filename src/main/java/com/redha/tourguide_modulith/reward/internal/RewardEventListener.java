package com.redha.tourguide_modulith.reward.internal;

import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardEventListener {

    private final RewardService rewardService;

    /**
     * Handles the VisitedLocationAddedEvent, triggered when a user visits a new location.
     * ➤ Origin: Emitted by UserService.handleTrackSuccess after the persistence of the visited location
     * ➤ Triggers reward calculation logic for the specified user.
     * ➤ Delegates to calculateRewards(UUID) for processing.
     */
    @EventListener
    public void handleVisitedLocationAdded(VisitedLocationAddedEvent event) {

        rewardService.calculateRewards(event.getUserId());

        log.info("✅ TRACKING COMPLETED - Location data successfully processed for the user : {}", event.getUserId());
    }

}
