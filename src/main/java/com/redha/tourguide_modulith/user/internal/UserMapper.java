package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.shared.UserDto;
import com.redha.tourguide_modulith.shared.UserPreferencesDto;
import com.redha.tourguide_modulith.shared.UserRewardDto;
import com.redha.tourguide_modulith.shared.UserTripDealDto;
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