package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.domain.AttractionDto;
import com.redha.tourguide_modulith.domain.LocationDto;
import com.redha.tourguide_modulith.domain.VisitedLocationDto;
import com.redha.tourguide_modulith.domain.UserDto;
import com.redha.tourguide_modulith.domain.UserRewardDto;
import com.redha.tourguide_modulith.user.internal.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private StartupInitializer startupInitializer;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private UserDto userDto;
    private Map<UUID, User> userMap;
    private VisitedLocationDto visitedLocation;
    private UserRewardDto userReward;
    private LocationDto locationDto;
    private AttractionDto attractionDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User(userId, "testUser", "123456789", "test@example.com");

        userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setUserName("testUser");
        userDto.setPhoneNumber("123456789");
        userDto.setEmailAddress("test@example.com");

        userMap = new HashMap<>();
        userMap.put(userId, user);

        LocationDto locationDto = new LocationDto(48.8584, 2.2945);
        visitedLocation = new VisitedLocationDto(userId, locationDto, new Date());

        AttractionDto attractionDto = new AttractionDto(
                51.5007,
                -0.1246,
                UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                "Big Ben",
                "London",
                "England"
        );

        attractionDto.attractionName = "Eiffel Tower";
        userReward = new UserRewardDto(visitedLocation, attractionDto, 100);

        lenient().when(startupInitializer.getInternalUserMap()).thenReturn(userMap);
        lenient().when(userMapper.toDto(user)).thenReturn(userDto);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Given
        UUID userId2 = UUID.randomUUID();
        User user2 = new User(userId2, "testUser2", "123456789", "test2@example.com");

        UserDto userDto2 = new UserDto();
        userDto2.setUserId(userId2);
        userDto2.setUserName("testUser2");
        userDto2.setPhoneNumber("123456789");
        userDto2.setEmailAddress("test2@example.com");

        userMap.put(user2.getUserId(), user2);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        verify(startupInitializer, times(1)).getInternalUserMap();
        verify(userMapper, times(1)).toDto(user);
        verify(userMapper, times(1)).toDto(user2);
    }

    @Test
    void getUser_shouldReturnUser() {
        // When
        UserDto result = userService.getUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getUserId(), result.getUserId());
        assertEquals(userDto.getUserName(), result.getUserName());
        verify(startupInitializer, times(1)).getInternalUserMap();
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void getUserInternal_shouldReturnInternalUser() {
        // When
        User result = userService.getUserInternal(userId);

        // Then
        assertEquals(user, result);
        verify(startupInitializer, times(1)).getInternalUserMap();
    }

    @Test
    void getVisitedLocations_shouldReturnUserVisitedLocations() {
        // Given
        List<VisitedLocationDto> visitedLocations = new ArrayList<>();
        visitedLocations.add(visitedLocation);

        // Add a visited location to the user
        user.addToVisitedLocations(visitedLocation);

        // When
        List<VisitedLocationDto> result = userService.getVisitedLocations(userId);

        // Then
        assertEquals(1, result.size());
        assertEquals(visitedLocation, result.getFirst());
    }

    @Test
    void getLastVisitedLocation_shouldReturnLastLocation() {
        // Given
        LocationDto locationDto1 = new LocationDto(48.8584, 2.2945);
        VisitedLocationDto location1 = new VisitedLocationDto(userId, locationDto1, new Date());

        LocationDto locationDto2 = new LocationDto(40.7128, -74.0060);
        VisitedLocationDto location2 = new VisitedLocationDto(userId, locationDto2, new Date());

        // Add visited locations to the user
        user.addToVisitedLocations(location1);
        user.addToVisitedLocations(location2);

        // When
        VisitedLocationDto result = userService.getLastVisitedLocation(userId);

        // Then
        assertEquals(location2, result);
    }

    @Test
    void addVisitedLocation_shouldAddLocationToUser() {
        // When
        userService.addVisitedLocation(userId, visitedLocation);

        // Then
        List<VisitedLocationDto> locations = user.getVisitedLocations();
        assertEquals(1, locations.size());
        assertEquals(visitedLocation, locations.getFirst());
    }

    @Test
    void clearVisitedLocations_shouldClearAllLocations() {
        // Given
        user.addToVisitedLocations(visitedLocation);
        assertFalse(user.getVisitedLocations().isEmpty());

        // When
        userService.clearVisitedLocations(userId);

        // Then
        assertTrue(user.getVisitedLocations().isEmpty());
    }

    @Test
    void getUserRewards_shouldReturnUserRewards() {
        // Given
        List<UserRewardDto> rewards = new ArrayList<>();
        rewards.add(userReward);

        // Add a reward to the user
        user.addToUserRewards(userReward);

        // When
        List<UserRewardDto> result = userService.getUserRewards(userId);

        // Then
        assertEquals(1, result.size());
        assertEquals(userReward, result.getFirst());
    }

    @Test
    void addUserRewards_shouldAddRewardToUser() {
        // When
        userService.addUserRewards(userId, userReward);

        // Then
        List<UserRewardDto> rewards = user.getUserRewards();
        assertEquals(1, rewards.size());
        assertEquals(userReward, rewards.getFirst());
    }
}