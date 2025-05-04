package com.redha.tourguide_modulith.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventDebugger {

    /**
     * Capture et log TOUS les événements qui transitent dans l'application
     * L'annotation @Order(Integer.MIN_VALUE) garantit que ce listener est exécuté en premier
     */
    @Order(Integer.MIN_VALUE)
    @EventListener
    public void debugAllEvents(Object event) {
        log.info("🔍 EVENT : {} - {} - Detail : {}",
                event.getClass().getSimpleName(),
                event.getClass().getName(),
                event);
    }
}