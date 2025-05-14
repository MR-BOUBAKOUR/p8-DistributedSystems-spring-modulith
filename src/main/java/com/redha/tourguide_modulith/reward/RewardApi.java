package com.redha.tourguide_modulith.reward;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface RewardApi {

    int getRewardPoints(UUID attractionId, UUID userId);
    void calculateRewards(UUID userId);
    CompletableFuture<Void> calculateRewardsAsync(UUID userId);
}
