package com.redha.tourguide_modulith.trip.internal;

import org.springframework.stereotype.Component;

import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

import static com.redha.tourguide_modulith.config.AppDefaultConst.TRIP_PRICER_API_KEY;

@Component
public class TripPricerAdapter {
    private final TripPricer tripPricer;

    public TripPricerAdapter() {
        this.tripPricer = new TripPricer();
    }

    public List<Provider> getPrice(
            UUID userId,
            int numberOfAdults,
            int numberOfChildren,
            int tripDuration,
            int cumulativeRewardPoints) {

        List<tripPricer.Provider> providers = tripPricer.getPrice(
                TRIP_PRICER_API_KEY,
                userId,
                numberOfAdults,
                numberOfChildren,
                tripDuration,
                cumulativeRewardPoints);

        return providers.stream()
                .map(this::convertProvider)
                .toList();
    }

    private Provider convertProvider(tripPricer.Provider extProvider) {
        return new Provider(
                extProvider.tripId,
                extProvider.name,
                extProvider.price
        );
    }
}
