package com.redha.tourguide_modulith.location;

import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.Location;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;

import java.util.List;
import java.util.UUID;

public interface LocationApi {

    VisitedLocation getUserLocation(UUID userId);

    VisitedLocation trackUserLocation(UUID userId);

    List<Attraction> getAttractions();

    boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

    double getDistance(Location loc1, Location loc2);

}
