package com.redha.tourguide_modulith.user.internal.model;

import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import lombok.Getter;
import lombok.Setter;

public class UserReward {

	public final VisitedLocationDto visitedLocation;
	public final AttractionDto attraction;

	@Getter @Setter
	private int rewardPoints;

	public UserReward(VisitedLocationDto visitedLocation, AttractionDto attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
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
