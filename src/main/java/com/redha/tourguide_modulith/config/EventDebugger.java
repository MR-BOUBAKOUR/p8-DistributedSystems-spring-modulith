package com.redha.tourguide_modulith.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventDebugger {

    /**
     * Captures and logs ALL events that pass through the application.
     * The @Order(Integer.MIN_VALUE) annotation ensures that this listener is executed first.
     */
    @Order(Integer.MIN_VALUE)
    @EventListener
    public void debugAllEvents(Object event) {
        log.info("🔍 EVENT : {} - Detail : {}",
                event.getClass().getSimpleName(),
                event);
    }
}