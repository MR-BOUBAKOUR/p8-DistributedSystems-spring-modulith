package com.redha.tourguide_modulith.location.dto;

import java.util.Date;
import java.util.UUID;

public class VisitedLocationDto {
    public UUID userId;
    public LocationDto location;
    public Date timeVisited;
}
