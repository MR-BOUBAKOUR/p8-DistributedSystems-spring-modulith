package com.redha.tourguide_modulith.reward.internal;

import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ApplicationModuleTest
class RewardEventListenerTest {

    private final RewardService rewardService;
    private final RewardEventListener rewardEventListener;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public RewardService rewardService() {
            return mock(RewardService.class);
        }

        @Bean
        public RewardEventListener rewardEventListener(RewardService rewardService) {
            return new RewardEventListener(rewardService);
        }
    }

    @Test
    void handleVisitedLocationAdded() {
        // given
        UUID userId = UUID.randomUUID();
        VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(48.8566, 2.3522), new Date());
        VisitedLocationAddedEvent event = new VisitedLocationAddedEvent(this, userId, visitedLocation);

        // when
        rewardEventListener.handleVisitedLocationAdded(event);

        // then
//        verify(rewardService, times(1)).calculateRewards(userId);
        verify(rewardService, times(1)).calculateRewardsAsync(userId);

    }
}