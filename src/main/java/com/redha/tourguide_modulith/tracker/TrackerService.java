package com.redha.tourguide_modulith.tracker;

import com.redha.tourguide_modulith.location.LocationApi;
import com.redha.tourguide_modulith.user.UserApi;
import com.redha.tourguide_modulith.user.internal.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.redha.tourguide_modulith.common.AppDefaultConst.TRACKING_POLLING_INTERVAL;

@Slf4j
@Service
public class TrackerService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final UserApi userApi;
    private final LocationApi locationApi;
    private boolean stop = false;

    public TrackerService(UserApi userApi, LocationApi locationApi) {
        this.userApi = userApi;
        this.locationApi = locationApi;
        startTracking();
    }

    private void startTracking() {
        executorService.submit(this::run);
    }

    public void stopTracking() {
        stop = true;
        executorService.shutdownNow();
    }

    private void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                log.debug("Tracker stopping");
                break;
            }

            List<User> users = userApi.getAllUsers();
            log.debug("Begin Tracker. Tracking {} users.", users.size());
            stopWatch.start();

//            List<CompletableFuture<VisitedLocation>> futures = new ArrayList<>();
//            users.forEach(user -> {
//                CompletableFuture<VisitedLocation> future = locationService.trackUserLocationAsync(user);
//                futures.add(future);
//            });
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            users.forEach(user -> locationApi.trackUserLocation(user.getUserId()));

            stopWatch.stop();
            log.debug("Tracker Time Elapsed: {} seconds.",
                    TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

            stopWatch.reset();
            try {
                log.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
