package com.redha.tourguide_modulith.reward.internal;

import org.springframework.stereotype.Component;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Component
public class RewardCentralAdapter {

    private final RewardCentral rewardCentral;

    public RewardCentralAdapter() {
        this.rewardCentral = new RewardCentral();
    }

    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }

}
