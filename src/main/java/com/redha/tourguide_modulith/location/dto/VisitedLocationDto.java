package com.redha.tourguide_modulith.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class VisitedLocationDto {
    public UUID userId;
    public LocationDto location;
    public Date timeVisited;
}
