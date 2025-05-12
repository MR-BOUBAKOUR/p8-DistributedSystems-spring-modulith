package com.redha.tourguide_modulith.reward.internal;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.shared.*;
import com.redha.tourguide_modulith.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ApplicationModuleTest
class RewardServiceTest {

    @MockitoBean
    private final RewardCentralAdapter rewardCentralAdapter;
    @MockitoBean
    private final LocationApi locationApi;
    @MockitoBean
    private final UserApi userApi;

    @Autowired
    private final RewardService rewardService;

    private UUID userId;
    private UUID attractionId;
    private UserDto userDto;
    private VisitedLocationDto visitedLocation;
    private AttractionDto attraction;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        attractionId = UUID.randomUUID();

        // Create a sample location
        visitedLocation = new VisitedLocationDto(
                userId,
                new LocationDto(40.7128, -74.0060),
                new Date()
        );

        // Create a sample attraction
        attraction = new AttractionDto(
                40.7129,
                -74.0061,
                attractionId,
                "Test Attraction",
                "Test City",
                "Test State"
        );

        // Create user DTO
        userDto = new UserDto(
                userId,
                "testUser",
                "123-456-7890",
                "test@example.com",
                new Date(),
                null,
                null,
                List.of(visitedLocation),
                null
        );
    }

    @Test
    void getRewardPoints_shouldReturnCorrectPoints() {
        // Given
        int expectedPoints = 100;
        when(rewardCentralAdapter.getAttractionRewardPoints(attractionId, userId))
                .thenReturn(expectedPoints);

        // When
        int actualPoints = rewardService.getRewardPoints(attractionId, userId);

        // Then
        assertEquals(expectedPoints, actualPoints);
        verify(rewardCentralAdapter, times(1))
                .getAttractionRewardPoints(attractionId, userId);
    }

    @Test
    void calculateRewards_whenNearAttraction_shouldAddUserReward() {
        // Given
        when(userApi.getUser(userId)).thenReturn(userDto);
        when(locationApi.getAttractions()).thenReturn(List.of(attraction));
        when(locationApi.nearAttraction(visitedLocation, attraction)).thenReturn(true);

        when(rewardCentralAdapter.getAttractionRewardPoints(attractionId, userId))
                .thenReturn(100);

        // When
        rewardService.calculateRewards(userId);

        // Then
        verify(userApi, times(1)).getUser(userId);
        verify(locationApi, times(1)).getAttractions();
        verify(locationApi, times(1)).nearAttraction(visitedLocation, attraction);

        // Verify that user reward is added
        verify(userApi, times(1)).addUserRewards(
                eq(userId),
                argThat(reward ->
                        reward.getVisitedLocation().equals(visitedLocation) &&
                        reward.getAttraction().equals(attraction) &&
                        reward.getRewardPoints() == 100
                )
        );
    }

    @Test
    void calculateRewards_whenNotNearAttraction_shouldNotAddUserReward() {
        // Given
        when(userApi.getUser(userId)).thenReturn(userDto);
        when(locationApi.getAttractions()).thenReturn(List.of(attraction));
        when(locationApi.nearAttraction(visitedLocation, attraction)).thenReturn(false);

        // When
        rewardService.calculateRewards(userId);

        // Then
        verify(userApi, times(1)).getUser(userId);
        verify(locationApi, times(1)).getAttractions();
        verify(locationApi, times(1)).nearAttraction(visitedLocation, attraction);

        // Verify that no user reward is added
        verify(userApi, never()).addUserRewards(
                eq(userId),
                any(UserRewardDto.class)
        );
    }
}