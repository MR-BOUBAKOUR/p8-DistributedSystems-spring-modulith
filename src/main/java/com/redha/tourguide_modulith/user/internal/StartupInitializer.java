package com.redha.tourguide_modulith.user.internal;

import com.redha.tourguide_modulith.shared.LocationDto;
import com.redha.tourguide_modulith.shared.VisitedLocationDto;
import com.redha.tourguide_modulith.user.UsersInitializedEvent;
import com.redha.tourguide_modulith.user.internal.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

import static com.redha.tourguide_modulith.config.AppDefaultConst.INTERNAL_USER_NUMBER;
import static com.redha.tourguide_modulith.config.AppDefaultConst.TEST_MODE;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupInitializer {

    private final ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Locale.setDefault(Locale.US);

        if (TEST_MODE) {
            log.info("=== Test Mode ENABLED ===");
            log.info(" >>> Starting user initialization <<< ");
            initializeInternalUsers();
            log.info(" >>> User initialization COMPLETED <<< ");

            eventPublisher.publishEvent(new UsersInitializedEvent(this));
        }
    }

    /**********************************************************************************
     * Methods Below: For Internal Testing
     **********************************************************************************/

    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes
    // internal users are provided and stored in memory
    @Getter
    private final Map<UUID, User> internalUserMap = new HashMap<>();

    void initializeInternalUsers() {
        IntStream.range(0, INTERNAL_USER_NUMBER).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(user.getUserId(), user);
        });
        log.info("Created {} internal test users.", INTERNAL_USER_NUMBER);
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocationDto(
                    user.getUserId(),
                    new LocationDto(generateRandomLatitude(), generateRandomLongitude()),
                    getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
}
