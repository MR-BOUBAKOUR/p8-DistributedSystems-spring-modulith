package com.redha.tourguide_modulith.gateway;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.shared.NearbyAttractionDTO;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.reward.RewardApi;
import com.redha.tourguide_modulith.trip.TripApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.shared.UserRewardDto;
import com.redha.tourguide_modulith.shared.UserTripDealDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayApi gatewayApi;
    private final UserApi userApi;
    private final LocationApi locationApi;
    private final TripApi tripApi;
    private final RewardApi rewardApi;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @GetMapping("/location/{userId}")
    public VisitedLocationDto getUserLocation(@PathVariable UUID userId) {
        return locationApi.getUserLocation(userId);
    }

    @GetMapping("/location/async/{userId}")
    public CompletableFuture<VisitedLocationDto> getUserLocationAsync(@PathVariable UUID userId) {
        return locationApi.getUserLocationAsync(userId);
    }

    @GetMapping("/nearby-attractions/{userId}")
    public List<NearbyAttractionDTO> getNearbyAttractions(@PathVariable UUID userId) {
        return gatewayApi.getNearbyAttractionsWithRewards(userId);
    }

    @RequestMapping("/rewards/{userId}")
    public List<UserRewardDto> getRewards(@PathVariable UUID userId) {
        return userApi.getUserRewards(userId);
    }

    @RequestMapping("/visited-locations/{userId}")
    public List<VisitedLocationDto> getVisitedLocations(@PathVariable UUID userId) {
        return userApi.getVisitedLocations(userId);
    }

    @RequestMapping("/deals/{userId}")
    public List<UserTripDealDto> getTripDeals(@PathVariable UUID userId) {
        return tripApi.getTripDeals(userId);
    }

}
