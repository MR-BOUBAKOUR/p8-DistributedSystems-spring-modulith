package com.redha.tourguide_modulith.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class VisitedLocationDto {
    public UUID userId;
    public LocationDto location;
    public Date timeVisited;
}
