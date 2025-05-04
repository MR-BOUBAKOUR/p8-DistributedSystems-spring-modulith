package com.redha.tourguide_modulith;

import com.redha.tourguide_modulith.tracker.TrackerService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class TourguideModulithApplication {

	private final TrackerService trackerService;

	public static void main(String[] args) {
		SpringApplication.run(TourguideModulithApplication.class, args);
	}

	@PreDestroy
	public void preDestroy() {
		trackerService.stopTracking();
	}

}
