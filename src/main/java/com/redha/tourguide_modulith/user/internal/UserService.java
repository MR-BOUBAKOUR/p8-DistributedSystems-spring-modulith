package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.location.TrackSuccessEvent;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.VisitedLocationAddedEvent;
import com.redha.tourguide_modulith.user.internal.model.User;
import com.redha.tourguide_modulith.user.internal.model.UserReward;
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

    @Override
    @EventListener
    public void handleTrackSuccess(TrackSuccessEvent event) {

        UUID userId = event.getUserId();
        VisitedLocation visitedLocation = event.getVisitedLocation();

        addVisitedLocation(userId, visitedLocation);

        eventPublisher.publishEvent(new VisitedLocationAddedEvent(this, userId, visitedLocation));

    }

    public List<User> getAllUsers() {
        return new ArrayList<>(startupInitialize.getInternalUserMap().values());
    }


    public User getUser(UUID userId) {
        return startupInitialize.getInternalUserMap().get(userId);
    }

    public void addUser(User user) {
        if (!startupInitialize.getInternalUserMap().containsKey(user.getUserId())) {
            startupInitialize.getInternalUserMap().put(user.getUserId(), user);
        }
    }

    @Override
    public void addVisitedLocation(UUID userId, VisitedLocation location) {
        // On délègue le comportement à l'entité
        getUser(userId).addToVisitedLocations(location);
    }

    @Override
    public void clearVisitedLocations(UUID userId) {
        getUser(userId).clearVisitedLocations();
    }

    @Override
    public VisitedLocation getLastVisitedLocation(UUID userId) {
        // On délègue le comportement à l'entité
        return getUser(userId).getLastVisitedLocation();
    }

    @Override
    public void addUserRewards(UUID userId, UserReward userReward) {
        // On délègue le comportement à l'entité
        getUser(userId).addToUserRewards(userReward);
    }
}
