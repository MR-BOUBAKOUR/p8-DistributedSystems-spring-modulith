package com.redha.tourguide_modulith.tracker.internal;

import com.redha.tourguide_modulith.config.AppDefaultConst;
import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.shared.UserDto;
import com.redha.tourguide_modulith.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ApplicationModuleTest
class TrackerServiceTest {

    @MockitoBean
    private final UserApi userApi;
    @MockitoBean
    private final LocationApi locationApi;

    @Autowired
    private TrackerService trackerService;

    @BeforeAll
    static void overrideTrackingInterval() {
        AppDefaultConst.TRACKING_POLLING_INTERVAL = 1L;
    }

    private UserDto user1;
    private UserDto user2;


    @BeforeEach
    void setUp() {

        trackerService = new TrackerService(userApi, locationApi);

        user1 = new UserDto(
                UUID.randomUUID(),
                "user1",
                "123-456-7890",
                "user1@example.com",
                new Date(),
                null,
                null,
                null,
                null
        );

        user2 = new UserDto(
                UUID.randomUUID(),
                "user2",
                "123-456-7890",
                "user2@example.com",
                new Date(),
                null,
                null,
                null,
                null
        );
    }

    @AfterAll
    void resetTrackingInterval() {
        AppDefaultConst.TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES.toSeconds(5);
    }

    @Test
    void startTracking_shouldInvokeLocationTrackingForAllUsers() throws InterruptedException {
        // Given
        when(userApi.getAllUsers()).thenReturn(List.of(user1, user2));

        // When
        trackerService.startTracking(); // triggered by UsersInitializedEvent
        TimeUnit.SECONDS.sleep(2); // wait to allow background thread to run at least once

        // Then
        verify(locationApi, atLeastOnce()).trackUserLocation(user1.getUserId());
        verify(locationApi, atLeastOnce()).trackUserLocation(user2.getUserId());

        // Cleanup
        trackerService.stopTracking();
    }

    @Test
    void stopTracking_shouldStopTheTrackingLoop() throws InterruptedException {
        // Given
        when(userApi.getAllUsers()).thenReturn(List.of(user1));

        // When
        trackerService.startTracking();
        TimeUnit.SECONDS.sleep(1);
        trackerService.stopTracking();
        int callCountAfterStop = mockingDetails(locationApi).getInvocations().size();
        TimeUnit.SECONDS.sleep(2);
        int callCountAfterWait = mockingDetails(locationApi).getInvocations().size();

        assertEquals(callCountAfterStop, callCountAfterWait, "No additional invocations after stopping tracking");
    }

    @Test
    void testTrackingIntervalUpdateForTheTestsOnly() throws InterruptedException {
        // Given
        List<UserDto> users = List.of(user1);
        when(userApi.getAllUsers()).thenReturn(users);

        // When
        long startTime = System.currentTimeMillis();
        trackerService.startTracking();
        TimeUnit.SECONDS.sleep(2);
        trackerService.stopTracking();
        long endTime = System.currentTimeMillis();

        // Then
        long elapsedTime = endTime - startTime;
        assertTrue(elapsedTime >= AppDefaultConst.TRACKING_POLLING_INTERVAL * 1000);
    }
}