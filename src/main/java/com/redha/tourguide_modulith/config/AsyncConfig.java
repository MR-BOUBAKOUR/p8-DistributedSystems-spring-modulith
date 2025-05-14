package com.redha.tourguide_modulith.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class AsyncConfig {

    @Bean
    public TaskExecutor customTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int cores = Runtime.getRuntime().availableProcessors();

        executor.setCorePoolSize(cores * 10);
        executor.setMaxPoolSize(cores * 20);
        executor.setQueueCapacity(500);

        executor.setThreadNamePrefix("TourGuide-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
