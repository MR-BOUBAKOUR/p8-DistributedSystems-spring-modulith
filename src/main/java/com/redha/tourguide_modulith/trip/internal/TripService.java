package com.redha.tourguide_modulith.trip.internal;

import com.redha.tourguide_modulith.trip.TripApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.internal.model.User;
import com.redha.tourguide_modulith.user.internal.model.UserPreferences;
import com.redha.tourguide_modulith.user.internal.model.UserReward;
import com.redha.tourguide_modulith.user.internal.model.UserTripDeal;
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

        User user = userApi.getUser(userId);

        int cumulativeRewardPoints = user.getUserRewards().stream()
                .mapToInt(UserReward::getRewardPoints)
                .sum();

        UserPreferences preferences = user.getUserPreferences();

        List<Provider> providers = tripPricerAdapter.getPrice(
                user.getUserId(),
                preferences.getNumberOfAdults(),
                preferences.getNumberOfChildren(),
                preferences.getTripDuration(),
                cumulativeRewardPoints);

        List<UserTripDeal> userTripDeals = providers.stream()
                .map(p -> new UserTripDeal(p.name(), p.price(), p.tripId()))
                .toList();

        user.setTripDeals(userTripDeals);

        return providers;
    }
}
