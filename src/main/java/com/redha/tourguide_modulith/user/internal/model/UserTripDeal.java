package com.redha.tourguide_modulith.user.internal.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserTripDeal {

    private final String name;
    private final double price;
    private final UUID tripId;

    public UserTripDeal(String name, double price, UUID tripId) {
        this.name = name;
        this.price = price;
        this.tripId = tripId;
    }
}
