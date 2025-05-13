package com.redha.tourguide_modulith.gateway.internal;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.reward.RewardApi;
import com.redha.tourguide_modulith.shared.NearbyAttractionDTO;
import com.redha.tourguide_modulith.trip.TripApi;
import com.redha.tourguide_modulith.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ApplicationModuleTest
class GatewayServiceTest {

    @MockitoBean
    private final LocationApi locationApi;
    @MockitoBean
    private final RewardApi rewardApi;
    @MockitoBean
    private final UserApi userApi;
    @MockitoBean
    private final TripApi tripApi;

    @Autowired
    private final GatewayService gatewayService;

    @Test
    void getNearbyAttractionsWithRewards() {
        // Given
        UUID userId = UUID.randomUUID();
        NearbyAttractionDTO attraction1 = new NearbyAttractionDTO(
                UUID.randomUUID(), "Attraction 1", 48.8566, 2.3522, 10.0, 48.8566, 2.3522, 0);
        NearbyAttractionDTO attraction2 = new NearbyAttractionDTO(
                UUID.randomUUID(), "Attraction 2", 48.8566, 2.3522, 15.0, 48.8566, 2.3522, 0);

        when(locationApi.getNearbyAttractions(userId)).thenReturn(Arrays.asList(attraction1, attraction2));
        when(rewardApi.getRewardPoints(attraction1.getAttractionId(), userId)).thenReturn(50);
        when(rewardApi.getRewardPoints(attraction2.getAttractionId(), userId)).thenReturn(100);

        // When
        List<NearbyAttractionDTO> attractionsWithRewards = gatewayService.getNearbyAttractionsWithRewards(userId);

        // Then
        assertNotNull(attractionsWithRewards);
        assertEquals(2, attractionsWithRewards.size());

        assertEquals(50, attractionsWithRewards.get(0).getRewardPoints());
        assertEquals(100, attractionsWithRewards.get(1).getRewardPoints());

        verify(locationApi).getNearbyAttractions(userId);
        verify(rewardApi).getRewardPoints(attraction1.getAttractionId(), userId);
        verify(rewardApi).getRewardPoints(attraction2.getAttractionId(), userId);
    }
}