package com.redha.tourguide_modulith.location;

import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.NearbyAttractionDTO;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LocationApi {

    VisitedLocationDto getUserLocation(UUID userId);
    CompletableFuture<VisitedLocationDto> getUserLocationAsync(UUID userId);

    VisitedLocationDto trackUserLocation(UUID userId);
    CompletableFuture<VisitedLocationDto> trackUserLocationAsync(UUID userId);

    List<AttractionDto> getAttractions();

    boolean nearAttraction(VisitedLocationDto visitedLocation, AttractionDto attraction);

    double getDistance(LocationDto loc1, LocationDto loc2);

    List<NearbyAttractionDTO> getNearbyAttractions(UUID userId);

}
