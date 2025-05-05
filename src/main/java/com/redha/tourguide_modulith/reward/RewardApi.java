package com.redha.tourguide_modulith.reward;

import java.util.UUID;

public interface RewardApi {

    int getRewardPoints(UUID attractionId, UUID userId);
    void calculateRewards(UUID userId);
}
