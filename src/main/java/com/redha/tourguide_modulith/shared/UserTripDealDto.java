package com.redha.tourguide_modulith.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserTripDealDto {
    private String name;
    private double price;
    private UUID tripId;
}