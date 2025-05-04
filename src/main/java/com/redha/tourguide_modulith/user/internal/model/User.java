package com.redha.tourguide_modulith.user.internal.model;

import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
public class User {

	private final UUID userId;
	private final String userName;

	@Setter
    private String phoneNumber;
	@Setter
    private String emailAddress;
    @Setter
    private Date latestLocationTimestamp;
    @Setter
    private UserPreferences userPreferences = new UserPreferences();
    @Setter
    private List<UserTripDeal> tripDeals = new ArrayList<>();

	private final List<VisitedLocation> visitedLocations = new ArrayList<>();
	private final List<UserReward> userRewards = new ArrayList<>();

	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

    public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}

	public void clearVisitedLocations() {
		visitedLocations.clear();
	}

	public VisitedLocation getLastVisitedLocation() {
		return visitedLocations.getLast();
	}

    public void addToUserRewards(UserReward userReward) {
		boolean attractionAlreadyRewarded = userRewards.stream()
				.anyMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName));

		if(!attractionAlreadyRewarded) {
			userRewards.add(userReward);
		}
	}

	@Override
	public String toString() {
		return "User{" +
				"userId=" + userId +
				", userName='" + userName + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", emailAddress='" + emailAddress + '\'' +
				", latestLocationTimestamp=" + latestLocationTimestamp +
				", visitedLocations=" + visitedLocations +
				", userRewards=" + userRewards +
				", userPreferences=" + userPreferences +
				", tripDeals=" + tripDeals +
				'}';
	}
}
