package com.redha.tourguide_modulith.location.internal;

import java.util.List;
import java.util.UUID;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.location.TrackSuccessEvent;
import com.redha.tourguide_modulith.location.dto.AttractionDto;
import com.redha.tourguide_modulith.location.dto.LocationDto;
import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import static com.redha.tourguide_modulith.common.AppDefaultConst.DEFAULT_PROXIMITY_BUFFER;
import static com.redha.tourguide_modulith.common.AppDefaultConst.STATUTE_MILES_PER_NAUTICAL_MILE;

@Slf4j
@Service
public class LocationService implements LocationApi {

    private final GpsUtilAdapter gpsUtilAdapter;
    private final ApplicationEventPublisher eventPublisher;
    private final LocationMapper locationMapper;

    public LocationService(GpsUtilAdapter gpsUtilAdapter, ApplicationEventPublisher eventPublisher, LocationMapper locationMapper) {
        this.gpsUtilAdapter = gpsUtilAdapter;
        this.eventPublisher = eventPublisher;
        this.locationMapper = locationMapper;
    }

    public VisitedLocationDto getUserLocation(UUID userId) {
        VisitedLocation visitedLocation = gpsUtilAdapter.getUserLocation(userId);
        return locationMapper.toDto(visitedLocation);
    }

    /**
     * Tracks the current location of the given user.
     * ➤ Retrieves the user's location from the GPS adapter (external service).
     * ➤ Publishes a TrackSuccessEvent to notify that the user's location has been successfully tracked.
     *    This event is handled by UserService to persist the location
     * ➤ The persistance will trigger VisitedLocationAddedEvent -> reward calculation.
     */
    public VisitedLocationDto trackUserLocation(UUID userId) {
        VisitedLocation visitedLocation = gpsUtilAdapter.getUserLocation(userId);
        VisitedLocationDto visitedLocationDto = locationMapper.toDto(visitedLocation);

        eventPublisher.publishEvent(new TrackSuccessEvent(this, userId, visitedLocationDto));

        log.info("✅ TRACKING COMPLETED - Location data successfully processed for user: {}", visitedLocationDto);

        return visitedLocationDto;
    }

//    public CompletableFuture<VisitedLocation> getUserLocationAsync(User user) {
//        return (user.getVisitedLocations().isEmpty())
//                ? trackUserLocationAsync(user)
//                : CompletableFuture.completedFuture(user.getLastVisitedLocation());
//    }
//
//    public CompletableFuture<VisitedLocation> trackUserLocationAsync(User user) {
//        return CompletableFuture
//                .supplyAsync(() -> {
//                    log.info("TrackUserLocationAsync for user: {} - Thread: {}",
//                            user.getUserName(), Thread.currentThread().getName());
//                    return gpsUtilAdapter.getUserLocation(user.getUserId());
//                }, customTaskExecutor)
//                .thenApply(visitedLocation -> {
//                    user.addToVisitedLocations(visitedLocation);
//                    return visitedLocation;
//                })
////                .thenCompose(visitedLocation -> {
////                    return rewardsService.calculateRewardsAsync(user)
////                            .thenApply(unused -> {
////                                log.info("getUserName: {} - getUserRewards: {}", user.getUserName(), user.getUserRewards());
////                                return visitedLocation;
////                            });
////                })
//                .exceptionally(ex -> {
//                    log.error("Error in trackUserLocationAsync: {}", ex.getMessage(), ex);
//                    return null;
//                });
//    }

    public List<AttractionDto> getAttractions() {
        List<Attraction> attractions = gpsUtilAdapter.getAttractions();
        return attractions.stream().map(locationMapper::toDto).toList();
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
