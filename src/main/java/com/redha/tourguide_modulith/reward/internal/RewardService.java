package com.redha.tourguide_modulith.reward.internal;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.reward.RewardApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.shared.UserDto;
import com.redha.tourguide_modulith.shared.UserRewardDto;
import com.redha.tourguide_modulith.user.internal.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@AllArgsConstructor
@Service
public class RewardService implements RewardApi {

    private final RewardCentralAdapter rewardCentralAdapter;
    private final LocationApi locationApi;
    private final UserApi userApi;
    private final TaskExecutor customTaskExecutor;

    public int getRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentralAdapter.getAttractionRewardPoints(
                attractionId, userId);
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

                    log.debug("💎💎💎💎💎 REWARD GRANTED to the user {}", user.getUserId());
                }
            }
        }

    }

    public CompletableFuture<Void> calculateRewardsAsync(UUID userId) {

        return CompletableFuture
                .supplyAsync(() -> userApi.getUser(userId), customTaskExecutor)
                .thenAcceptAsync(user -> {
                    log.info("CalculateRewardsAsync for user: {} - Thread: {}",
                            user.getUserName(), Thread.currentThread().getName());
                    calculateRewards(userId);
                }, customTaskExecutor)
                .exceptionally(ex -> {
                    log.error("Error in calculateRewardsAsync: {}", ex.getMessage(), ex);
                    return null;
                });

    }
}
