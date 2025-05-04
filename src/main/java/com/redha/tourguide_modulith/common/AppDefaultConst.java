package com.redha.tourguide_modulith.common;

import java.util.concurrent.TimeUnit;

public class AppDefaultConst {

    public static final boolean TEST_MODE = true;

    public static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES.toSeconds(1);

    public static final int INTERNAL_USER_NUMBER = 100;

    public static final int DEFAULT_PROXIMITY_BUFFER = 10;

    public static final int ATTRACTION_PROXIMITY_RANGE = 200;

    public static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    public static final String TRIP_PRICER_API_KEY = "test-server-api-key";

}