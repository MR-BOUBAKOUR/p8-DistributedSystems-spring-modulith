package com.redha.tourguide_modulith.user.event;

import org.springframework.context.ApplicationEvent;

public class UsersInitializedEvent extends ApplicationEvent {
    public UsersInitializedEvent(Object source) {
        super(source);
    }
}