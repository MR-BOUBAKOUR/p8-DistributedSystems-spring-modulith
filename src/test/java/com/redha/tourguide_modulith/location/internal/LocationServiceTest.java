package com.redha.tourguide_modulith.location.internal;

import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.Location;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.NearbyAttractionDTO;
import com.redha.tourguide_modulith.user.UserLocationTrackedEvent;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ApplicationModuleTest
public class LocationServiceTest {

    @MockitoBean
    private final GpsUtilAdapter gpsUtilAdapter;
    @MockitoBean
    private final ApplicationEventPublisher publisher;
    @MockitoBean
    private final LocationMapper locationMapper;
    @MockitoBean
    private final UserApi userApi;

    @Autowired
    private final LocationService locationService;

    @TestConfiguration
    static class TestConfig {

        // Required to ensure LocationService uses this mock instead of the real publisher.
        // Without it, the real publisher is used and verify(publisher).publishEvent(...) fails.
        @Bean
        @Primary
        public ApplicationEventPublisher publisher() {
            return mock(ApplicationEventPublisher.class);
        }

    }

    private UUID userId;
    private LocationDto locationDto;
    private VisitedLocation visitedLocation;
    private VisitedLocationDto visitedLocationDto;
    private List<Attraction> attractions;
    private List<AttractionDto> attractionsDto;

    @BeforeEach
    void setUp() {

        userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Location location = new Location(40.7128, -74.0060);
        locationDto = new LocationDto(40.7128, -74.0060);

        visitedLocation = new VisitedLocation(userId, location, new Date());
        visitedLocationDto = new VisitedLocationDto(userId, locationDto, new Date());

        // Setup attractions
        attractions = new ArrayList<>();
        attractions.add(new Attraction("Attraction 1", "City 1", "State 1", 40.7129, -74.0061));
        attractions.add(new Attraction("Attraction 2", "City 2", "State 2", 40.7000, -74.0100));
        attractions.add(new Attraction("Attraction 3", "City 3", "State 3", 40.7200, -74.0200));
        attractions.add(new Attraction("Attraction 4", "City 4", "State 4", 40.7300, -74.0300));
        attractions.add(new Attraction("Attraction 5", "City 5", "State 5", 40.7400, -74.0400));
        attractions.add(new Attraction("Attraction 6", "City 6", "State 6", 40.7600, -74.0500));

        attractionsDto = new ArrayList<>();
        for (int i = 0; i < attractions.size(); i++) {
            Attraction attraction = attractions.get(i);
            UUID fixedId = UUID.nameUUIDFromBytes(("attraction-" + i).getBytes());

            AttractionDto attractionDto = new AttractionDto(
                    attraction.latitude,
                    attraction.longitude,
                    fixedId,
                    attraction.attractionName,
                    attraction.city,
                    attraction.state
            );
            attractionsDto.add(attractionDto);

            when(locationMapper.toDto(attraction)).thenReturn(attractionDto);
        }

        when(locationMapper.toDto(visitedLocation)).thenReturn(visitedLocationDto);
    }

    @Test
    void getUserLocation_whenLocationsEmpty_shouldTrackUserLocation() {

        System.out.println("Test publisher: " + publisher);
        System.out.println("Service publisher: " + locationService.getPublisher());

        // Given
        when(userApi.getVisitedLocations(userId)).thenReturn(Collections.emptyList());
        when(gpsUtilAdapter.getUserLocation(userId)).thenReturn(visitedLocation);

        // When
        VisitedLocationDto result = locationService.getUserLocation(userId);

        // Then
        assertEquals(visitedLocationDto, result);
        verify(userApi, times(1)).getVisitedLocations(userId);
        verify(gpsUtilAdapter, times(1)).getUserLocation(userId);
        verify(publisher, times(1)).publishEvent(any(UserLocationTrackedEvent.class));
    }

    @Test
    void getUserLocation_whenLocationsNotEmpty_shouldReturnLastLocation() {
        // Given
        List<VisitedLocationDto> visitedLocations = List.of(visitedLocationDto);
        when(userApi.getVisitedLocations(userId)).thenReturn(visitedLocations);
        when(userApi.getLastVisitedLocation(userId)).thenReturn(visitedLocationDto);

        // When
        VisitedLocationDto result = locationService.getUserLocation(userId);

        // Then
        assertEquals(visitedLocationDto, result);
        verify(publisher, never()).publishEvent(any(UserLocationTrackedEvent.class));
        verify(userApi, times(1)).getVisitedLocations(userId);
        verify(userApi, times(1)).getLastVisitedLocation(userId);
        verify(gpsUtilAdapter, never()).getUserLocation(userId);
    }

    @Test
    void trackUserLocation_shouldPublishEventAndReturnLocation() {
        // Given
        when(gpsUtilAdapter.getUserLocation(userId)).thenReturn(visitedLocation);

        // When
        VisitedLocationDto result = locationService.trackUserLocation(userId);

        // Then
        assertEquals(visitedLocationDto, result);
        verify(gpsUtilAdapter, times(1)).getUserLocation(userId);
        verify(publisher, times(1)).publishEvent(any(UserLocationTrackedEvent.class));
    }

    @Test
    void getAttractions_shouldReturnAllAttractions() {
        // Given
        when(gpsUtilAdapter.getAttractions()).thenReturn(attractions);

        // When
        List<AttractionDto> result = locationService.getAttractions();

        // Then
        assertEquals(attractionsDto.size(), result.size());
        for (int i = 0; i < attractionsDto.size(); i++) {
            assertEquals(attractionsDto.get(i), result.get(i));
        }
        verify(gpsUtilAdapter, times(1)).getAttractions();
    }

    @Test
    void nearAttraction_whenUserIsNear_shouldReturnTrue() {
        // Given
        AttractionDto nearbyAttraction = attractionsDto.getFirst(); // First attraction is close by

        // When
        boolean result = locationService.nearAttraction(visitedLocationDto, nearbyAttraction);

        // Then
        assertTrue(result);
    }

    @Test
    void nearAttraction_whenUserIsFar_shouldReturnFalse() {
        // Given
        AttractionDto farAttraction = new AttractionDto(
                90.0,
                0.0,
                UUID.randomUUID(),
                "Far Attraction",
                "North Pole",
                "Arctic"
        );

        // When
        boolean result = locationService.nearAttraction(visitedLocationDto, farAttraction);

        // Then
        assertFalse(result);
    }

    @Test
    void getDistance_shouldCalculateCorrectDistance() {
        // Given
        LocationDto loc1 = new LocationDto(40.7128, -74.0060);
        LocationDto loc2 = new LocationDto(34.0522, -118.2437);

        // When
        double distance = locationService.getDistance(loc1, loc2);

        // Then
        // The distance between NY and LA is approximately 2,448 miles
        assertTrue(distance > 2400 && distance < 2500);
    }

    @Test
    void getNearbyAttractions_shouldReturnFiveClosestAttractions() {
        // Given
        when(userApi.getVisitedLocations(userId)).thenReturn(Collections.emptyList());
        when(gpsUtilAdapter.getUserLocation(userId)).thenReturn(visitedLocation);
        when(gpsUtilAdapter.getAttractions()).thenReturn(attractions);

        // When
        List<NearbyAttractionDTO> result = locationService.getNearbyAttractions(userId);

        // Then
        assertEquals(5, result.size());

        // First attraction is close by
        assertEquals(attractionsDto.getFirst().attractionId, result.getFirst().attractionId);
        assertEquals(attractionsDto.getFirst().attractionName, result.getFirst().attractionName);
        assertEquals(attractionsDto.getFirst().latitude, result.getFirst().attractionLatitude);
        assertEquals(attractionsDto.getFirst().longitude, result.getFirst().attractionLongitude);
        assertEquals(locationDto.latitude, result.getFirst().userLatitude);
        assertEquals(locationDto.longitude, result.getFirst().userLongitude);

        verify(gpsUtilAdapter, times(1)).getAttractions();
    }
}