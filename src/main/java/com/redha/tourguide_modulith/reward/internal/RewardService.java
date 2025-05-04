package com.redha.tourguide_modulith.reward.internal;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.location.dto.AttractionDto;
import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
import com.redha.tourguide_modulith.reward.RewardApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.dto.UserDto;
import com.redha.tourguide_modulith.user.dto.UserRewardDto;
import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class RewardService implements RewardApi {

    private final RewardCentralAdapter rewardCentralAdapter;
    private final LocationApi locationApi;
    private final UserApi userApi;

    /**
     * Handles the VisitedLocationAddedEvent, triggered when a user visits a new location.
     * ➤ Origin: Emitted by UserService.handleTrackSuccess after the persistence of the visited location
     * ➤ Triggers reward calculation logic for the specified user.
     * ➤ Delegates to calculateRewards(UUID) for processing.
     */
    @EventListener(VisitedLocationAddedEvent.class)
    public void handleVisitedLocationAdded(VisitedLocationAddedEvent event) {

        UUID userId = event.getUserId();

        log.info("📤 VisitedLocationAddedEvent published - User: {}", userId);

        calculateRewards(userId);
    }

    public void calculateRewards(UUID userId) {
        UserDto user = userApi.getUser(userId);

        List<VisitedLocationDto> visitedLocations = new ArrayList<>(user.getVisitedLocations());
        List<AttractionDto> attractions = locationApi.getAttractions();

        for(VisitedLocationDto visitedLocation : visitedLocations) {
            for(AttractionDto attraction : attractions) {
                if(locationApi.nearAttraction(visitedLocation, attraction)) {
                    userApi.addUserRewards(userId,
                            new UserRewardDto(
                                    visitedLocation,
                                    attraction,
                                    getRewardPoints(attraction.getAttractionId(), user.getUserId()))
                    );

                    log.info("💎 REWARD GRANTED - User: {}, Attraction: {}, Points: {}, Location: (lat={}, lon={})",
                            userId,
                            attraction.getAttractionName(),
                            getRewardPoints(attraction.getAttractionId(), userId),
                            visitedLocation.getLocation().getLatitude(),
                            visitedLocation.getLocation().getLongitude());
                    log.info("✅ REWARD CALCULATION COMPLETED - User: {}, Total Rewards Granted: {}",
                            userId,
                            user.getUserRewards().size());
                }
            }
        }

    }

//    public CompletableFuture<Void> calculateRewardsAsync(UUID userId) {
//        User user = userApi.getUser(userId);
//
//        return CompletableFuture
//                .runAsync(() -> {
//                    log.info("CalculateRewardsAsync for user: {} - Thread: {}", user.getUserName(), Thread.currentThread().getName());
//                    calculateRewards(userId);
//                }, customTaskExecutor)
//                .exceptionally(ex -> {
//                    log.error("Error in calculateRewardsAsync: {}", ex.getMessage(), ex);
//                    return null;
//                });
//    }

    private int getRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentralAdapter.getAttractionRewardPoints(
                attractionId, userId);
    }
}
