package com.redha.tourguide_modulith.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttractionDto extends LocationDto {
    public String attractionName;
    public String city;
    public String state;
    public UUID attractionId;

    public AttractionDto(double latitude, double longitude, String attractionName, String city, String state, UUID attractionId) {
        super(latitude, longitude);
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = attractionId;
    }
}
