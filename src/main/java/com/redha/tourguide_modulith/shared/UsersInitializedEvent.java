package com.redha.tourguide_modulith.shared;

import org.springframework.context.ApplicationEvent;

public class UsersInitializedEvent extends ApplicationEvent {
    public UsersInitializedEvent(Object source) {
        super(source);
    }
}