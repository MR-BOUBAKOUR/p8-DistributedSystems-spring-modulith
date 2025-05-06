package com.redha.tourguide_modulith.location.internal;

import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
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
