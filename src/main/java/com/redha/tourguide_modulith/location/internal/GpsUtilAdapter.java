package com.redha.tourguide_modulith.location.internal;

import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.Location;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import gpsUtil.GpsUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class GpsUtilAdapter {
    private final GpsUtil gpsUtil;

    public GpsUtilAdapter() {
        this.gpsUtil = new GpsUtil();
    }

    public VisitedLocation getUserLocation(UUID userId) {
        gpsUtil.location.VisitedLocation visitedLocation = gpsUtil.getUserLocation(userId);
        return convertVisitedLocation(visitedLocation);
    }

    public List<Attraction> getAttractions() {
        List<gpsUtil.location.Attraction> attractions = gpsUtil.getAttractions();
        return attractions.stream()
                .map(this::convertAttraction)
                .toList();
    }

    private VisitedLocation convertVisitedLocation(gpsUtil.location.VisitedLocation extVisitedLocation) {

        Location internalLocation = new Location(
                extVisitedLocation.location.latitude,
                extVisitedLocation.location.longitude);

        return new VisitedLocation(
                extVisitedLocation.userId,
                internalLocation,
                new Date(extVisitedLocation.timeVisited.getTime())
        );
    }

    private Attraction convertAttraction(gpsUtil.location.Attraction extAttraction) {
        return new Attraction(
                extAttraction.attractionName,
                extAttraction.city,
                extAttraction.state,
                extAttraction.latitude,
                extAttraction.longitude
        );
    }
}
