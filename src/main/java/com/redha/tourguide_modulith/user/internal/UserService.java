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

    @Override
    @EventListener
    public void handleTrackSuccess(TrackSuccessEvent event) {


        UUID userId = event.getUserId();
        VisitedLocationDto visitedLocation = event.getVisitedLocation();

        addVisitedLocation(userId, visitedLocation);

        eventPublisher.publishEvent(new VisitedLocationAddedEvent(this, userId, visitedLocation));

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
