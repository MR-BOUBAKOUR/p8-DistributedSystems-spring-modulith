package com.redha.tourguide_modulith.trip;

import com.redha.tourguide_modulith.shared.UserTripDealDto;

import java.util.List;
import java.util.UUID;

public interface TripApi {

    List<UserTripDealDto> getTripDeals(UUID userId);

}
