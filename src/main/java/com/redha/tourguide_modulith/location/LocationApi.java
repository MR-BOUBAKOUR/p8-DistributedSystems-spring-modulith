package com.redha.tourguide_modulith.location;

import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.NearbyAttractionDTO;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;

import java.util.List;
import java.util.UUID;

public interface LocationApi {

    VisitedLocationDto getUserLocation(UUID userId);

    VisitedLocationDto trackUserLocation(UUID userId);

    List<AttractionDto> getAttractions();

    boolean nearAttraction(VisitedLocationDto visitedLocation, AttractionDto attraction);

    double getDistance(LocationDto loc1, LocationDto loc2);

    List<NearbyAttractionDTO> getNearbyAttractions(UUID userId);
}
