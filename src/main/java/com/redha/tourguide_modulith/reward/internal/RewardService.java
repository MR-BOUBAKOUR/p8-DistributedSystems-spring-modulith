package com.redha.tourguide_modulith.reward.internal;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import com.redha.tourguide_modulith.reward.RewardApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.internal.model.User;
import com.redha.tourguide_modulith.user.internal.model.UserReward;
import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RewardService implements RewardApi {

    private final RewardCentralAdapter rewardCentralAdapter;
    private final LocationApi locationApi;
    private final UserApi userApi;
    private final TaskExecutor customTaskExecutor;

    public RewardService(RewardCentralAdapter rewardCentralAdapter, LocationApi locationApi, UserApi userApi, TaskExecutor customTaskExecutor) {
        this.rewardCentralAdapter = rewardCentralAdapter;
        this.locationApi = locationApi;
        this.userApi = userApi;
        this.customTaskExecutor = customTaskExecutor;
    }

    @Override
    @EventListener
    public void handleVisitedLocationAdded(VisitedLocationAddedEvent event) {
        UUID userId = event.getUserId();
        calculateRewards(userId);
    }

    public void calculateRewards(UUID userId) {
        User user = userApi.getUser(userId);

        List<VisitedLocation> visitedLocations = new ArrayList<>(user.getVisitedLocations());
        List<Attraction> attractions = locationApi.getAttractions();

        for(VisitedLocation visitedLocation : visitedLocations) {
            for(Attraction attraction : attractions) {
                if(locationApi.nearAttraction(visitedLocation, attraction)) {
                    user.addToUserRewards(
                            new UserReward(
                                    visitedLocation,
                                    attraction,
                                    getRewardPoints(attraction.attractionId, user.getUserId()))
                    );
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
