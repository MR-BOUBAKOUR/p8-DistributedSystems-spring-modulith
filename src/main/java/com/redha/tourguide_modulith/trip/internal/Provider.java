package com.redha.tourguide_modulith.trip.internal;

import java.util.UUID;

public record Provider(UUID tripId, String name, double price) {
}