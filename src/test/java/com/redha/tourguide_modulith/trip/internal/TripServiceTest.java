package com.redha.tourguide_modulith.trip.internal;

import com.redha.tourguide_modulith.shared.UserDto;
import com.redha.tourguide_modulith.shared.UserPreferencesDto;
import com.redha.tourguide_modulith.shared.UserRewardDto;
import com.redha.tourguide_modulith.shared.UserTripDealDto;
import com.redha.tourguide_modulith.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ApplicationModuleTest
class TripServiceTest {

    @MockitoBean
    private final TripPricerAdapter tripPricerAdapter;
    @MockitoBean
    private final UserApi userApi;

    @Autowired
    private final TripService tripService;

    private UUID userId;
    private UserDto userDto;
    private UserPreferencesDto userPreferences;
    private List<UserRewardDto> userRewards;
    private List<Provider> providers;

    @BeforeEach
    void setUp() {
        // Setup test data
        userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // Create user rewards
        userRewards = List.of(
                new UserRewardDto(null, null, 100),
                new UserRewardDto(null, null, 200)
        );

        // Create user preferences
        userPreferences = new UserPreferencesDto(
                1,
                20,
                1,
                1,
                1
        );

        // Create providers
        providers = List.of(
                new Provider(UUID.randomUUID(), "Provider 1", 100.0),
                new Provider(UUID.randomUUID(), "Provider 2", 150.0)
        );

        // Create user DTO
        userDto = new UserDto(
                userId,
                "testUser",
                "123-456-7890",
                "test@example.com",
                new Date(),
                userPreferences,
                null, // initial trip deals
                null, // visited locations
                userRewards
        );
    }

    @Test
    void getTripDeals_shouldReturnCorrectTripDeals() {
        // Given
        when(userApi.getUser(userId)).thenReturn(userDto);
        when(tripPricerAdapter.getPrice(
                eq(userId),
                eq(userPreferences.getNumberOfAdults()),
                eq(userPreferences.getNumberOfChildren()),
                eq(userPreferences.getTripDuration()),
                eq(300)))
                .thenReturn(providers);

        // When
        List<UserTripDealDto> result = tripService.getTripDeals(userId);

        System.out.println(result);
        System.out.println(providers);
        // Then
        // Verify the number of trip deals
        assertEquals(providers.size(), result.size());

        // Verify each trip deal matches the provider
        for (int i = 0; i < providers.size(); i++) {
            Provider provider = providers.get(i);
            UserTripDealDto tripDeal = result.get(i);

            assertEquals(provider.name(), tripDeal.getName());
            assertEquals(provider.price(), tripDeal.getPrice());
            assertEquals(provider.tripId(), tripDeal.getTripId());
        }

        // Verify interactions
        verify(userApi, times(1)).getUser(userId);
        verify(tripPricerAdapter, times(1)).getPrice(
                eq(userId),
                eq(1),
                eq(1),
                eq(20),
                eq(300)
        );
    }

    @Test
    void getTripDeals_withNoRewards_shouldUseZeroPoints() {
        // Given
        UserDto userWithNoRewards = new UserDto(
                userId,
                "testUser",
                "123-456-7890",
                "test@example.com",
                new Date(),
                userPreferences,
                null,
                null,
                List.of() // Empty rewards
        );

        when(userApi.getUser(userId)).thenReturn(userWithNoRewards);
        when(tripPricerAdapter.getPrice(
                eq(userId),
                eq(userPreferences.getNumberOfAdults()),
                eq(userPreferences.getNumberOfChildren()),
                eq(userPreferences.getTripDuration()),
                eq(0)
        )).thenReturn(providers);

        // When
        List<UserTripDealDto> result = tripService.getTripDeals(userId);

        // Then
        assertNotNull(result);
        assertEquals(providers.size(), result.size());

        // Verify each trip deal matches the provider
        for (int i = 0; i < providers.size(); i++) {
            Provider provider = providers.get(i);
            UserTripDealDto tripDeal = result.get(i);

            assertEquals(provider.name(), tripDeal.getName());
            assertEquals(provider.price(), tripDeal.getPrice());
            assertEquals(provider.tripId(), tripDeal.getTripId());
        }

        // Verify interactions
        verify(userApi, times(1)).getUser(userId);
        verify(tripPricerAdapter, times(1)).getPrice(
                eq(userId),
                eq(1),
                eq(1),
                eq(20),
                eq(0)
        );
    }
}