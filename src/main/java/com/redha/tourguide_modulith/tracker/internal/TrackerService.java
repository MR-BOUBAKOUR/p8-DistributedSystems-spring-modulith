package com.redha.tourguide_modulith.tracker.internal;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.tracker.TrackerApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.UsersInitializedEvent;
import com.redha.tourguide_modulith.shared.UserDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.redha.tourguide_modulith.config.AppDefaultConst.TRACKING_POLLING_INTERVAL;

@Slf4j
@Service
public class TrackerService implements TrackerApi {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final UserApi userApi;
    private final LocationApi locationApi;

    private boolean stop = false;
    private boolean isTrackingStarted = false;

    public TrackerService(UserApi userApi, LocationApi locationApi) {
        this.userApi = userApi;
        this.locationApi = locationApi;
    }

    @EventListener(UsersInitializedEvent.class)
    public void startTracking() {
        if (!isTrackingStarted) {
            isTrackingStarted = true;
            executorService.submit(this::run);
        }
    }

    @PreDestroy
    public void stopTracking() {
        log.info("Stopping tracker during application shutdown");
        stop = true;
        executorService.shutdownNow();
    }

    private void run() {
        StopWatch stopWatch = new StopWatch();

        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                log.info("Tracker stopping");
                break;
            }

            List<UserDto> users = userApi.getAllUsers();

            log.info("Begin Tracker. Tracking {} users.", users.size());
            stopWatch.start();

            users.forEach(user -> locationApi.trackUserLocation(user.getUserId()));

//            List<CompletableFuture<VisitedLocation>> futures = new ArrayList<>();
//            users.forEach(user -> {
//                CompletableFuture<VisitedLocation> future = locationService.trackUserLocationAsync(user);
//                futures.add(future);
//            });
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();


            stopWatch.stop();
            log.info("Tracker Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

            stopWatch.reset();
            try {
                log.info("Tracker sleeping");
                TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
