package com.redha.tourguide_modulith.trip.internal;

import com.redha.tourguide_modulith.trip.TripApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.dto.UserDto;
import com.redha.tourguide_modulith.user.dto.UserPreferencesDto;
import com.redha.tourguide_modulith.user.dto.UserRewardDto;
import com.redha.tourguide_modulith.user.dto.UserTripDealDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TripService implements TripApi {

    private final TripPricerAdapter tripPricerAdapter;
    private final UserApi userApi;

    public TripService(TripPricerAdapter tripPricerAdapter, UserApi userApi) {
        this.tripPricerAdapter = tripPricerAdapter;
        this.userApi = userApi;
    }

    public List<Provider> getTripDeals(UUID userId) {

        UserDto user = userApi.getUser(userId);

        int cumulativeRewardPoints = user.getUserRewards().stream()
                .mapToInt(UserRewardDto::getRewardPoints)
                .sum();

        UserPreferencesDto preferences = user.getUserPreferences();

        List<Provider> providers = tripPricerAdapter.getPrice(
                user.getUserId(),
                preferences.getNumberOfAdults(),
                preferences.getNumberOfChildren(),
                preferences.getTripDuration(),
                cumulativeRewardPoints);

        List<UserTripDealDto> userTripDeals = providers.stream()
                .map(p -> new UserTripDealDto(p.name(), p.price(), p.tripId()))
                .toList();

        user.setTripDeals(userTripDeals);

        return providers;
    }
}
