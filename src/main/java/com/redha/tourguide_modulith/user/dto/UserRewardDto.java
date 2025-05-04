package com.redha.tourguide_modulith.user.dto;

import com.redha.tourguide_modulith.location.dto.AttractionDto;
import com.redha.tourguide_modulith.location.dto.VisitedLocationDto;
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