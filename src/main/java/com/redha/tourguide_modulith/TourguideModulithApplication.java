package com.redha.tourguide_modulith;

import com.redha.tourguide_modulith.tracker.TrackerService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.Modulith;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@Modulith
@ConfigurationPropertiesScan
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
