package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.UserLocationTrackedEvent;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;


@RequiredArgsConstructor
@ApplicationModuleTest
class UserEventListenerTest {

    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        public ApplicationEventPublisher applicationEventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }
    }

    @Test
    void testHandleTrackSuccess() {

        UUID userId = UUID.randomUUID();

        VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(48.8566, 2.3522), new Date());

        UserLocationTrackedEvent event = new UserLocationTrackedEvent(this, userId, visitedLocation);

        UserEventListener userEventListener = new UserEventListener(userService, applicationEventPublisher);

        userEventListener.handleTrackSuccess(event);

        verify(userService, times(1)).addVisitedLocation(userId, visitedLocation);

        verify(applicationEventPublisher, times(1)).publishEvent(any(VisitedLocationAddedEvent.class));

    }
}
