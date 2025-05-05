package com.redha.tourguide_modulith.location.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class NearbyAttractionDTO {
    public UUID attractionId;
    public String attractionName;
    public double attractionLatitude;
    public double attractionLongitude;
    public double distanceInMiles;
    public double userLatitude;
    public double userLongitude;
    @Setter
    public int rewardPoints;

    public NearbyAttractionDTO(UUID attractionId, String attractionName, double attractionLatitude, double attractionLongitude,
                               double distanceInMiles, double userLatitude, double userLongitude, int rewardPoints) {
        this.attractionId = attractionId;
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.distanceInMiles = distanceInMiles;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.rewardPoints = rewardPoints;
    }

}
