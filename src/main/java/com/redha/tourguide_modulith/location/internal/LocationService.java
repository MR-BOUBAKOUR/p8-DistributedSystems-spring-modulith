package com.redha.tourguide_modulith.location.internal;

import java.util.List;
import java.util.UUID;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.location.TrackSuccessEvent;
import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.Location;
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
    private final TaskExecutor customTaskExecutor;
    private final ApplicationEventPublisher eventPublisher;

    public LocationService(GpsUtilAdapter gpsUtilAdapter, TaskExecutor customTaskExecutor, ApplicationEventPublisher eventPublisher) {
        this.gpsUtilAdapter = gpsUtilAdapter;
        this.customTaskExecutor = customTaskExecutor;
        this.eventPublisher = eventPublisher;
    }

    public VisitedLocation getUserLocation(UUID userId) {
        return gpsUtilAdapter.getUserLocation(userId);
    }

    public VisitedLocation trackUserLocation(UUID userId) {
        VisitedLocation visitedLocation = gpsUtilAdapter.getUserLocation(userId);

        eventPublisher.publishEvent(new TrackSuccessEvent(this, userId, visitedLocation));

        return visitedLocation;
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

    public List<Attraction> getAttractions() {
        return gpsUtilAdapter.getAttractions();
    }

    public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction, visitedLocation.location())
                > DEFAULT_PROXIMITY_BUFFER);
    }

    public double getDistance(Location loc1, Location loc2) {
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
