package com.redha.tourguide_modulith.location.internal;

import com.redha.tourguide_modulith.domain.AttractionDto;
import com.redha.tourguide_modulith.domain.LocationDto;
import com.redha.tourguide_modulith.domain.VisitedLocationDto;
import com.redha.tourguide_modulith.location.internal.model.Attraction;
import com.redha.tourguide_modulith.location.internal.model.Location;
import com.redha.tourguide_modulith.location.internal.model.VisitedLocation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toDto(Location location);
    Location fromDto(LocationDto dto);

    VisitedLocationDto toDto(VisitedLocation visitedLocation);
    VisitedLocation fromDto(VisitedLocationDto dto);

    AttractionDto toDto(Attraction attraction);
    Attraction fromDto(AttractionDto dto);
}
