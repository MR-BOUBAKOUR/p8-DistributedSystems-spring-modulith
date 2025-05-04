package com.redha.tourguide_modulith.user.internal.model;

import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import lombok.Getter;
import lombok.Setter;

public class UserReward {

	public final VisitedLocation visitedLocation;
	public final Attraction attraction;

	@Getter @Setter
	private int rewardPoints;
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}

	@Override
	public String toString() {
		return "UserReward{" +
				"visitedLocation=" + visitedLocation +
				", attraction=" + attraction +
				", rewardPoints=" + rewardPoints +
				'}';
	}
}
