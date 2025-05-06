package com.redha.tourguide_modulith.shared;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPreferencesDto {
    private int attractionProximity;
    private int tripDuration;
    private int ticketQuantity;
    private int numberOfAdults;
    private int numberOfChildren;
}
