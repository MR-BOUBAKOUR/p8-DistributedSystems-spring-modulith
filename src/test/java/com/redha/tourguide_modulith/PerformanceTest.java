package com.redha.tourguide_modulith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.redha.tourguide_modulith.location.internal.LocationService;
import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import com.redha.tourguide_modulith.reward.internal.RewardService;
import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.UserDto;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.tracker.internal.TrackerService;
import com.redha.tourguide_modulith.trip.internal.TripService;
import com.redha.tourguide_modulith.user.internal.StartupInitializer;
import com.redha.tourguide_modulith.user.internal.UserService;
import com.redha.tourguide_modulith.user.internal.model.User;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PerformanceTest {

    @Autowired
    private LocationService locationService;
    @Autowired
    private RewardService rewardService;
    @Autowired
    private TrackerService trackerService;
    @Autowired
    private TripService tripService;
    @Autowired
    private UserService userService;
    @Autowired
    private StartupInitializer startupInitializer;

    // ===============================================================================================
    // CHANGE the number of the generated users in " config - AppDefaultConst - INTERNAL_USER_NUMBER "
    // ===============================================================================================

    /*
     * A note on performance improvements:
     *
     * The number of users generated for the high volume tests can be easily
     * adjusted via this method:
     *
     * InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     * These tests can be modified to suit new solutions, just as long as the
     * performance metrics at the end of the tests remains consistent.
     *
     * These are performance metrics that we are trying to hit:
     *
     * highVolumeTrackLocation: 100,000 users within 15 minutes:
     * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     * highVolumeGetRewards: 100,000 users within 20 minutes:
     * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */

    @Test
    public void highVolumeTrackLocation() {

        // Users should be incremented up to 100,000, and test finishes within 15 minutes

        // ===============================================================================================
        // CHANGE the number of the generated users in " config - AppDefaultConst - INTERNAL_USER_NUMBER "
        // ===============================================================================================

        List<UserDto> allUsers = new ArrayList<>();
        allUsers = userService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (UserDto user : allUsers) {
            locationService.trackUserLocation(user.getUserId());
        }
        stopWatch.stop();
        trackerService.stopTracking();

        System.out.println("highVolumeTrackLocation: Time Elapsed: "
                + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Disabled
    @Test
    public void highVolumeGetRewards() {

        // Users should be incremented up to 100,000, and test finishes within 20 minutes

        // ===============================================================================================
        // CHANGE the number of the generated users in " config - AppDefaultConst - INTERNAL_USER_NUMBER "
        // ===============================================================================================

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Attraction attraction = locationService.getAttractionsInternal().getFirst();
        LocationDto locationDto = new LocationDto(attraction.latitude, attraction.longitude);
        List<User> allUsers = new ArrayList<>();
        allUsers = userService.getAllUsersInternal();
        allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocationDto(u.getUserId(), locationDto, new Date())));

        allUsers.forEach(u -> rewardService.calculateRewards(u.getUserId()));

        for (User user : allUsers) {
            assertFalse(user.getUserRewards().isEmpty());
        }
        stopWatch.stop();
        trackerService.stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}