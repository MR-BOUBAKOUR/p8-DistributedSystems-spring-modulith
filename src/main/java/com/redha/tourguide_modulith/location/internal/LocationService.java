package com.redha.tourguide_modulith.location.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.shared.*;
import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import com.redha.tourguide_modulith.user.UserApi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import static com.redha.tourguide_modulith.config.AppDefaultConst.DEFAULT_PROXIMITY_BUFFER;
import static com.redha.tourguide_modulith.config.AppDefaultConst.STATUTE_MILES_PER_NAUTICAL_MILE;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService implements LocationApi {

    private final GpsUtilAdapter gpsUtilAdapter;
    @Getter
    private final ApplicationEventPublisher publisher;
    private final LocationMapper locationMapper;
    private final UserApi userApi;
    private final TaskExecutor customTaskExecutor;

    public VisitedLocationDto getUserLocation(UUID userId) {

        return (userApi.getVisitedLocations(userId).isEmpty())
                ? trackUserLocation(userId)
                : userApi.getLastVisitedLocation(userId);

    }

    public CompletableFuture<VisitedLocationDto> getUserLocationAsync(UUID userId) {
        List<VisitedLocationDto> locations = userApi.getVisitedLocations(userId);

        return locations.isEmpty()
                ? trackUserLocationAsync(userId)
                : CompletableFuture.completedFuture(locations.getLast());
    }

    public VisitedLocationDto trackUserLocation(UUID userId) {
        VisitedLocation visitedLocation = gpsUtilAdapter.getUserLocation(userId);
        VisitedLocationDto visitedLocationDto = locationMapper.toDto(visitedLocation);
        publisher.publishEvent(new UserLocationTrackedEvent(this, userId, visitedLocationDto));

        return visitedLocationDto;
    }

    /**
     * Tracks the current location of the given user.
     * ➤ Retrieves the user's location from the GPS adapter - external service.
     * ➤ Publishes a UserLocationTrackedEvent to notify that the user's location has been successfully tracked.
     *    This event is handled by UserService -> persist the new location of the user.
     * ➤ The persistance will trigger VisitedLocationAddedEvent
     *    This event is handled by RewardService -> calculate the reward
     *    (if the new location is near an attraction - external service).
     */
    public CompletableFuture<VisitedLocationDto> trackUserLocationAsync(UUID userId) {

        return CompletableFuture
                .supplyAsync(() -> {
                    log.info("TrackUserLocationAsync for user: {} - Thread: {}", userId, Thread.currentThread().getName());
                    VisitedLocation visitedLocation = gpsUtilAdapter.getUserLocation(userId);
                    return locationMapper.toDto(visitedLocation);
                }, customTaskExecutor)
                .thenApply(visitedLocationDto -> {
                    // user.addToVisitedLocations(visitedLocationDto);
                    publisher.publishEvent(new UserLocationTrackedEvent(this, userId, visitedLocationDto));
                    return visitedLocationDto;
                })
                .exceptionally(ex -> {
                    log.error("Error in trackUserLocationAsync: {}", ex.getMessage(), ex);
                    return null;
                });

    }

    public List<NearbyAttractionDTO> getNearbyAttractions(UUID userId) {
        List<NearbyAttractionDTO> nearbyAttractions = new ArrayList<>();

        VisitedLocationDto visitedLocation = getUserLocation(userId);

        List<AttractionDto> fiveNearbyAttractions = getAttractions().stream()
                .sorted(Comparator.comparingDouble(attraction -> getDistance(attraction, visitedLocation.location)))
                .limit(5)
                .toList();

        for (AttractionDto nearbyAttraction : fiveNearbyAttractions) {
            double distance = getDistance(nearbyAttraction, visitedLocation.location);

            nearbyAttractions.add(new NearbyAttractionDTO(
                    nearbyAttraction.attractionId,
                    nearbyAttraction.attractionName,
                    nearbyAttraction.latitude,
                    nearbyAttraction.longitude,
                    distance,
                    visitedLocation.location.latitude,
                    visitedLocation.location.longitude,
                 0
            ));
        }

        return nearbyAttractions;
    }

    public List<AttractionDto> getAttractions() {
        List<Attraction> attractions = gpsUtilAdapter.getAttractions();
        return attractions.stream().map(locationMapper::toDto).toList();
    }

    public List<Attraction> getAttractionsInternal() {
        return gpsUtilAdapter.getAttractions();
    }

    public boolean nearAttraction(VisitedLocationDto visitedLocation, AttractionDto attraction) {
        return !(getDistance(visitedLocation.location, attraction)
                > DEFAULT_PROXIMITY_BUFFER);
    }

    public double getDistance(LocationDto loc1, LocationDto loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);

        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }
}
