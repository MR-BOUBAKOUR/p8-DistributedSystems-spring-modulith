package com.redha.tourguide_modulith.trip;

import com.redha.tourguide_modulith.trip.internal.Provider;

import java.util.List;
import java.util.UUID;

public interface TripApi {

    List<Provider> getTripDeals(UUID userId);

}
