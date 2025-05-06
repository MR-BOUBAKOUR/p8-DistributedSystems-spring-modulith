package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.domain.UserDto;
import com.redha.tourguide_modulith.domain.UserPreferencesDto;
import com.redha.tourguide_modulith.domain.UserRewardDto;
import com.redha.tourguide_modulith.domain.UserTripDealDto;
import com.redha.tourguide_modulith.user.internal.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);
    User toEntity(UserDto dto);

    UserPreferencesDto toDto(UserPreferences preferences);
    UserPreferences toEntity(UserPreferencesDto dto);

    UserRewardDto toDto(UserReward reward);
    UserReward toEntity(UserRewardDto dto);

    UserTripDealDto toDto(UserTripDeal deal);
    UserTripDeal toEntity(UserTripDealDto dto);
}