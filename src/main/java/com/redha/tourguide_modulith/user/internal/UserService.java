package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.location.TrackSuccessEvent;
import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import com.redha.tourguide_modulith.user.dto.UserDto;
import com.redha.tourguide_modulith.user.dto.UserRewardDto;
import com.redha.tourguide_modulith.user.internal.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;

import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class UserService implements UserApi {

    private StartupInitializer startupInitialize;
    private ApplicationEventPublisher eventPublisher;
    private UserMapper userMapper;

    /**
     * Handles the TrackSuccessEvent triggered after a user's location has been successfully tracked.
     * ➤ Origin: Emitted by LocationService.trackUserLocation(UUID userId) after GPS data is processed.
     * ➤ Adds the visited location to the user's history.
     * ➤ Then emits a VisitedLocationAddedEvent to trigger downstream actions -> reward calculation.
     */
    @EventListener(TrackSuccessEvent.class)
    public void handleTrackSuccess(TrackSuccessEvent event) {

        UUID userId = event.getUserId();

        log.info("📩 TrackSuccessEvent received - User: {}", userId);

        VisitedLocationDto visitedLocation = event.getVisitedLocation();

        addVisitedLocation(userId, visitedLocation);

        eventPublisher.publishEvent(new VisitedLocationAddedEvent(this, userId, visitedLocation));

        log.info("📍 VISITED LOCATION ADDED - User: {}, Location: (lat={}, lon={}), Time: {}",
                userId,
                visitedLocation.getLocation().getLatitude(),
                visitedLocation.getLocation().getLongitude(),
                visitedLocation.getTimeVisited());

    }

    public List<UserDto> getAllUsers() {
        return startupInitialize.getInternalUserMap().values().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUser(UUID userId) {
        User user = startupInitialize.getInternalUserMap().get(userId);
        return userMapper.toDto(user);
    }

    public User getUserInternal(UUID userId) {
        return startupInitialize.getInternalUserMap().get(userId);
    }

    public void addUser(UserDto userDto) {

        User user = userMapper.toEntity(userDto);

        if (!startupInitialize.getInternalUserMap().containsKey(user.getUserId())) {
            startupInitialize.getInternalUserMap().put(user.getUserId(), user);
        }
    }

    @Override
    public void addVisitedLocation(UUID userId, VisitedLocationDto location) {
        // On délègue le comportement à l'entité
        getUserInternal(userId).addToVisitedLocations(location);
    }

    @Override
    public void clearVisitedLocations(UUID userId) {
        getUserInternal(userId).clearVisitedLocations();
    }

    @Override
    public VisitedLocationDto getLastVisitedLocation(UUID userId) {
        // On délègue le comportement à l'entité
        return getUserInternal(userId).getLastVisitedLocation();
    }

    @Override
    public void addUserRewards(UUID userId, UserRewardDto userReward) {
        // On délègue le comportement à l'entité
        getUserInternal(userId).addToUserRewards(userReward);
    }
}
