package com.redha.tourguide_modulith.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserTripDealDto {
    private String name;
    private double price;
    private UUID tripId;
}