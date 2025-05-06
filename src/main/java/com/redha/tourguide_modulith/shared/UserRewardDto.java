package com.redha.tourguide_modulith.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRewardDto {
    private VisitedLocationDto visitedLocation;
    private AttractionDto attraction;
    private int rewardPoints;
}