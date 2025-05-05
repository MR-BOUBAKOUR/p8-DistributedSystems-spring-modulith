package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.dto.UserDto;
import com.redha.tourguide_modulith.user.dto.UserRewardDto;
import com.redha.tourguide_modulith.user.internal.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class UserService implements UserApi {

    private final StartupInitializer startupInitialize;
    private final UserMapper userMapper;

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

    public List<VisitedLocationDto> getVisitedLocations(UUID userId) {
        return getUserInternal(userId).getVisitedLocations();
    }

    public VisitedLocationDto getLastVisitedLocation(UUID userId) {
        return getUserInternal(userId).getLastVisitedLocation();
    }

    public void addVisitedLocation(UUID userId, VisitedLocationDto location) {
        getUserInternal(userId).addToVisitedLocations(location);
    }

    public void clearVisitedLocations(UUID userId) {
        getUserInternal(userId).clearVisitedLocations();
    }

    public void addUserRewards(UUID userId, UserRewardDto userReward) {
        getUserInternal(userId).addToUserRewards(userReward);
    }
}
