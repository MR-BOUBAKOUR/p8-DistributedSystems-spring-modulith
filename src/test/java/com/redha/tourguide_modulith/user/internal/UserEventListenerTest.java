package com.redha.tourguide_modulith.user.internal;


import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.user.UserLocationTrackedEvent;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.user.UserVisitedLocationAddedEvent;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;


@RequiredArgsConstructor
@ApplicationModuleTest
class UserEventListenerTest {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final UserEventListener userEventListener;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        @Primary
        public ApplicationEventPublisher applicationEventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }

        @Bean
        public UserEventListener userEventListener(UserService userService, ApplicationEventPublisher publisher) {
            return new UserEventListener(userService, publisher);
        }
    }

    @Test
    void testHandleUserLocationTrackedEvent() {
        // given
        UUID userId = UUID.randomUUID();
        VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(48.8566, 2.3522), new Date());
        UserLocationTrackedEvent event = new UserLocationTrackedEvent(this, userId, visitedLocation);

        // when
        userEventListener.handleUserLocationTrackedEvent(event);

        // then
        verify(userService, times(1)).addVisitedLocation(userId, visitedLocation);
        verify(publisher, times(1)).publishEvent(any(UserVisitedLocationAddedEvent.class));
    }
}
