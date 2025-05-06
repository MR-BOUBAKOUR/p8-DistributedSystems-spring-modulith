package com.redha.tourguide_modulith.user;

import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
import com.redha.tourguide_modulith.user.dto.UserDto;
import com.redha.tourguide_modulith.user.dto.UserRewardDto;
import com.redha.tourguide_modulith.user.dto.UserTripDealDto;

import java.util.List;
import java.util.UUID;

public interface UserApi {

    // Méthodes de service

    UserDto getUser(UUID userId);
    List<UserDto> getAllUsers();
    void addUser(UserDto user);

    // Méthodes qui touchent au comportement des entités

    void addVisitedLocation(UUID userId, VisitedLocationDto location);
    void clearVisitedLocations(UUID userId);
    VisitedLocationDto getLastVisitedLocation(UUID userId);
    List<VisitedLocationDto> getVisitedLocations(UUID userId);

    List<UserRewardDto> getUserRewards(UUID userId);
    void addUserRewards(UUID userId, UserRewardDto userReward);

}
