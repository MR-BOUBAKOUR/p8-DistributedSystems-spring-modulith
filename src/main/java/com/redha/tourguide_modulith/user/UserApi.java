package com.redha.tourguide_modulith.user;

import com.redha.tourguide_modulith.location.TrackSuccessEvent;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import com.redha.tourguide_modulith.user.internal.model.User;
import com.redha.tourguide_modulith.user.internal.model.UserReward;

import java.util.List;
import java.util.UUID;

public interface UserApi {

    void handleTrackSuccess(TrackSuccessEvent event);

    // Méthodes de service

    User getUser(UUID userId);
    List<User> getAllUsers();
    void addUser(User user);

    // Méthodes qui touchent au comportement des entités

    void addVisitedLocation(UUID userId, VisitedLocation location);
    void clearVisitedLocations(UUID userId);
    VisitedLocation getLastVisitedLocation(UUID userId);

    void addUserRewards(UUID userId, UserReward userReward);
}
