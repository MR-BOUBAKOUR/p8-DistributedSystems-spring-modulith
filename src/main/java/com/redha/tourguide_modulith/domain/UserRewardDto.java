package com.redha.tourguide_modulith.domain;

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