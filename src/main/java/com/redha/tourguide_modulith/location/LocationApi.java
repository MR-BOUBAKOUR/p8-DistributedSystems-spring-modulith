package com.redha.tourguide_modulith.location;

import com.redha.tourguide_modulith.location.dto.AttractionDto;
import com.redha.tourguide_modulith.location.dto.LocationDto;
import com.redha.tourguide_modulith.location.dto.NearbyAttractionDTO;
import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.Location;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;

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
