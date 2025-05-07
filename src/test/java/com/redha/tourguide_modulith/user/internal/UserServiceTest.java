package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.shared.AttractionDto;
import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.shared.UserDto;
import com.redha.tourguide_modulith.shared.UserRewardDto;
import com.redha.tourguide_modulith.user.internal.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ApplicationModuleTest
public class UserServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public StartupInitializer startupInitializer() {
            return mock(StartupInitializer.class);
        }

        @Bean
        public UserMapper userMapper() {
            return mock(UserMapper.class);
        }
    }

    @Autowired
    private StartupInitializer startupInitializer;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    private UUID userId;
    private User user;
    private UserDto userDto;
    private VisitedLocationDto visitedLocation;
    private UserRewardDto userReward;

    @BeforeEach
    void setUp() {
        reset(startupInitializer);
        reset(userMapper);

        userId = UUID.randomUUID();
        user = new User(userId, "testUser", "123456789", "test@example.com");

        userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setUserName("testUser");
        userDto.setPhoneNumber("123456789");
        userDto.setEmailAddress("test@example.com");

        Map<UUID, User> userMap = new HashMap<>();
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

        userReward = new UserRewardDto(visitedLocation, attractionDto, 100);

        when(startupInitializer.getInternalUserMap()).thenReturn(userMap);
        when(userMapper.toDto(user)).thenReturn(userDto);
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

        Map<UUID, User> updatedMap = new HashMap<>();
        updatedMap.put(userId, user);
        updatedMap.put(userId2, user2);

        when(startupInitializer.getInternalUserMap()).thenReturn(updatedMap);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        verify(startupInitializer, times(1)).getInternalUserMap();
    }

    @Test
    void getUser_shouldReturnUser() {
        // When
        UserDto result = userService.getUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getUserName(), result.getUserName());
    }

    @Test
    void getUserInternal_shouldReturnInternalUser() {
        // When
        User result = userService.getUserInternal(userId);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getUserId(), result.getUserId());
        assertEquals(userDto.getUserName(), result.getUserName());
    }

    @Test
    void getVisitedLocations_shouldReturnUserVisitedLocations() {
        // Given
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
        LocationDto firstLocationDto = new LocationDto(48.8584, 2.2945);
        VisitedLocationDto firstVisitedLocation = new VisitedLocationDto(userId, firstLocationDto, new Date());

        LocationDto lastLocationDto = new LocationDto(40.7128, -74.0060);
        VisitedLocationDto lastVisitedLocation = new VisitedLocationDto(userId, lastLocationDto, new Date());

        user.addToVisitedLocations(firstVisitedLocation);
        user.addToVisitedLocations(lastVisitedLocation);

        // When
        // depends on the last one added -> visitedLocations.getLast()
        VisitedLocationDto result = userService.getLastVisitedLocation(userId);

        // Then
        assertEquals(lastVisitedLocation, result);
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