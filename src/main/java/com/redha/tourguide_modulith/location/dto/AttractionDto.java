package com.redha.tourguide_modulith.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttractionDto extends LocationDto {
    public UUID attractionId;
    public String attractionName;
    public String city;
    public String state;

    public AttractionDto(double latitude, double longitude, UUID attractionId, String attractionName, String city, String state) {
        super(latitude, longitude);
        this.attractionId = attractionId;
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
    }
}
