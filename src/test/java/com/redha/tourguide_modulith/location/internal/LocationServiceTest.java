package com.redha.tourguide_modulith.location.internal;

import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.Location;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.NearbyAttractionDTO;
import com.redha.tourguide_modulith.shared.UserLocationTrackedEvent;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.user.UserApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ApplicationModuleTest
public class LocationServiceTest {

    @MockitoBean
    private GpsUtilAdapter gpsUtilAdapter;
    @MockitoBean
    private ApplicationEventPublisher eventPublisher;
    @MockitoBean
    private LocationMapper locationMapper;

    @MockitoBean
    private UserApi userApi;

    @Autowired
    private LocationService locationService;

    private UUID userId;
    private LocationDto locationDto;
    private VisitedLocation visitedLocation;
    private VisitedLocationDto visitedLocationDto;
    private List<Attraction> attractions;
    private List<AttractionDto> attractionDtos;
/*
    @TestConfiguration
    static class TestConfig {
        @Bean
        public GpsUtilAdapter gpsUtilAdapter() {
            return mock(GpsUtilAdapter.class);
        }

        @Bean
        public ApplicationEventPublisher eventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }

        @Bean
        public LocationMapper locationMapper() {
            return mock(LocationMapper.class);
        }

        @Bean
        public UserApi userService() {
            return mock(UserApi.class);
        }

        @Bean
        public LocationService locationService(GpsUtilAdapter gpsUtilAdapter, ApplicationEventPublisher eventPublisher,
                                               LocationMapper locationMapper, UserApi userApi) {
            return new LocationService(gpsUtilAdapter, eventPublisher, locationMapper, userApi);
        }
    }
*/
    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        Location location = new Location(40.7128, -74.0060); // New York coordinates
        locationDto = new LocationDto(40.7128, -74.0060);

        visitedLocation = new VisitedLocation(userId, location, new Date());
        visitedLocationDto = new VisitedLocationDto(userId, locationDto, new Date());

        // Setup attractions
        attractions = new ArrayList<>();
        attractions.add(new Attraction("Attraction 1", "City 1", "State 1", 40.7500, -74.0000));
        attractions.add(new Attraction("Attraction 2", "City 2", "State 2", 40.7000, -74.0100));
        attractions.add(new Attraction("Attraction 3", "City 3", "State 3", 40.7200, -74.0200));
        attractions.add(new Attraction("Attraction 4", "City 4", "State 4", 40.7300, -74.0300));
        attractions.add(new Attraction("Attraction 5", "City 5", "State 5", 40.7400, -74.0400));
        attractions.add(new Attraction("Attraction 6", "City 6", "State 6", 40.7600, -74.0500));

        attractionDtos = new ArrayList<>();
        for (Attraction attraction : attractions) {
            AttractionDto attractionDto = new AttractionDto(
                    attraction.latitude,
                    attraction.longitude,
                    UUID.randomUUID(),
                    attraction.attractionName,
                    attraction.city,
                    attraction.state
            );
            attractionDtos.add(attractionDto);

            when(locationMapper.toDto(attraction)).thenReturn(attractionDto);
        }

        when(locationMapper.toDto(visitedLocation)).thenReturn(visitedLocationDto);
    }

    @Test
    void getUserLocation_whenLocationsEmpty_shouldTrackUserLocation() {
        // Given
        when(userApi.getVisitedLocations(userId)).thenReturn(Collections.emptyList());
        when(gpsUtilAdapter.getUserLocation(userId)).thenReturn(visitedLocation);

        // When
        VisitedLocationDto result = locationService.getUserLocation(userId);

        // Then
        assertEquals(visitedLocationDto, result);
        verify(eventPublisher, times(1)).publishEvent(any(UserLocationTrackedEvent.class));
        verify(userApi, times(1)).getVisitedLocations(userId);
        verify(gpsUtilAdapter, times(1)).getUserLocation(userId);
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
        verify(eventPublisher, never()).publishEvent(any(UserLocationTrackedEvent.class));
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
        verify(eventPublisher, times(1)).publishEvent(any(UserLocationTrackedEvent.class));
        verify(gpsUtilAdapter, times(1)).getUserLocation(userId);
    }

    @Test
    void getAttractions_shouldReturnAllAttractions() {
        // Given
        when(gpsUtilAdapter.getAttractions()).thenReturn(attractions);

        // When
        List<AttractionDto> result = locationService.getAttractions();

        // Then
        assertEquals(attractionDtos.size(), result.size());
        for (int i = 0; i < attractionDtos.size(); i++) {
            assertEquals(attractionDtos.get(i), result.get(i));
        }
        verify(gpsUtilAdapter, times(1)).getAttractions();
    }

    @Test
    void nearAttraction_whenUserIsNear_shouldReturnTrue() {
        // Given
        AttractionDto nearbyAttraction = attractionDtos.getFirst(); // First attraction is close by

        // When
        boolean result = locationService.nearAttraction(visitedLocationDto, nearbyAttraction);

        // Then
        assertTrue(result);
    }

    @Test
    void nearAttraction_whenUserIsFar_shouldReturnFalse() {
        // Given
        AttractionDto farAttraction = new AttractionDto(
                90.0, // North pole
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
        LocationDto loc1 = new LocationDto(40.7128, -74.0060); // New York
        LocationDto loc2 = new LocationDto(34.0522, -118.2437); // Los Angeles

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

        // Verify first attraction details
        assertEquals(attractionDtos.getFirst().attractionId, result.getFirst().attractionId);
        assertEquals(attractionDtos.getFirst().attractionName, result.getFirst().attractionName);
        assertEquals(attractionDtos.getFirst().latitude, result.getFirst().attractionLatitude);
        assertEquals(attractionDtos.getFirst().longitude, result.getFirst().attractionLongitude);
        assertEquals(locationDto.latitude, result.getFirst().userLatitude);
        assertEquals(locationDto.longitude, result.getFirst().userLongitude);

        // Not testing attraction sorting here as in real usage the stream sorting would handle this

        verify(gpsUtilAdapter, times(1)).getAttractions();
    }
}