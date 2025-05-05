package com.redha.tourguide_modulith.location.internal;

import com.redha.tourguide_modulith.location.dto.NearbyAttractionDTO;
import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/{userId}")
    public VisitedLocationDto getUserLocation(@PathVariable UUID userId) {
        return locationService.getUserLocation(userId);
    }

//    @GetMapping("/{userId}/async")
//    public CompletableFuture<VisitedLocationDto> getUserLocationAsync(@PathVariable UUID userId) {
//        return locationService.getUserLocationAsync(userId);
//    }
//
    @GetMapping("/{userId}/nearby-attractions")
    public List<NearbyAttractionDTO> getNearbyAttractions(@PathVariable UUID userId) {
        return locationService.getNearbyAttractions(userId);
    }

}
