package com.redha.tourguide_modulith.gateway;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.location.dto.NearbyAttractionDTO;
import com.redha.tourguide_modulith.reward.RewardApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private final LocationApi locationApi;
    private final RewardApi rewardApi;

    public List<NearbyAttractionDTO> getNearbyAttractionsWithRewards(UUID userId) {

        List<NearbyAttractionDTO> nearbyAttractions = locationApi.getNearbyAttractions(userId);

        nearbyAttractions.forEach(attraction -> {
            int rewardPoints = rewardApi.getRewardPoints(attraction.getAttractionId(), userId);
            attraction.setRewardPoints(rewardPoints);
        });

        return nearbyAttractions;
    }

}
