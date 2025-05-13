package com.redha.tourguide_modulith.gateway;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.reward.RewardApi;
import com.redha.tourguide_modulith.shared.*;
import com.redha.tourguide_modulith.trip.TripApi;
import com.redha.tourguide_modulith.user.UserApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GatewayController.class)
public class GatewayControllerTest {

    @MockitoBean
    private GatewayApi gatewayApi;

    @MockitoBean
    private UserApi userApi;

    @MockitoBean
    private LocationApi locationApi;

    @MockitoBean
    private TripApi tripApi;

    @MockitoBean
    private RewardApi rewardApi;

    @Autowired
    private MockMvc mockMvc;

    private UUID userId;
    private VisitedLocationDto visitedLocationDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        LocationDto location = new LocationDto(48.8566, 2.3522);
        visitedLocationDto = new VisitedLocationDto(userId, location, new Date());
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Greetings from TourGuide!"));
    }

    @Test
    void testGetUserLocation() throws Exception {
        when(locationApi.getUserLocation(userId)).thenReturn(visitedLocationDto);

        mockMvc.perform(get("/api/location/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.location.latitude").value(48.8566))
                .andExpect(jsonPath("$.location.longitude").value(2.3522));
    }

    @Test
    void testGetNearbyAttractions() throws Exception {
        UUID userId = UUID.randomUUID();
        List<NearbyAttractionDTO> attractions = List.of(
                new NearbyAttractionDTO(UUID.randomUUID(), "Attraction 1", 48.8566, 2.3522, 10.0, 48.8566, 2.3522, 0),
                new NearbyAttractionDTO(UUID.randomUUID(), "Attraction 2", 48.8566, 2.3522, 15.0, 48.8566, 2.3522, 0)
        );

        when(gatewayApi.getNearbyAttractionsWithRewards(userId)).thenReturn(attractions);

        mockMvc.perform(get("/api/nearby-attractions/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attractionName").value("Attraction 1"))
                .andExpect(jsonPath("$[1].attractionName").value("Attraction 2"))
                .andExpect(jsonPath("$[0].distanceInMiles").value(10.0))
                .andExpect(jsonPath("$[1].distanceInMiles").value(15.0));
    }

    @Test
    void testGetRewards() throws Exception {
        AttractionDto attraction = new AttractionDto(48.8566, 2.3522, UUID.randomUUID(), "Eiffel Tower", "Paris", "Ile-de-France");
        UserRewardDto rewardDto = new UserRewardDto(visitedLocationDto, attraction, 100);

        when(userApi.getUserRewards(userId)).thenReturn(List.of(rewardDto));

        mockMvc.perform(get("/api/rewards/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].visitedLocation.userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].rewardPoints").value(100));
    }

    @Test
    void testGetVisitedLocations() throws Exception {
        when(userApi.getVisitedLocations(userId)).thenReturn(List.of(visitedLocationDto));

        mockMvc.perform(get("/api/visited-locations/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].location.latitude").value(48.8566))
                .andExpect(jsonPath("$[0].location.longitude").value(2.3522));
    }

    @Test
    void testGetTripDeals() throws Exception {
        UserTripDealDto deal = new UserTripDealDto("Best Travel Co", 1999.99, UUID.randomUUID());

        when(tripApi.getTripDeals(userId)).thenReturn(List.of(deal));

        mockMvc.perform(get("/api/deals/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Best Travel Co"))
                .andExpect(jsonPath("$[0].price").value(1999.99))
                .andExpect(jsonPath("$[0].tripId").exists());
    }
}
