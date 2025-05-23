package com.redha.tourguide_modulith.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private UUID userId;
    private String userName;
    private String phoneNumber;
    private String emailAddress;
    private Date latestLocationTimestamp;
    private UserPreferencesDto userPreferences;
    private List<UserTripDealDto> tripDeals;
    private List<VisitedLocationDto> visitedLocations;
    private List<UserRewardDto> userRewards;
}
