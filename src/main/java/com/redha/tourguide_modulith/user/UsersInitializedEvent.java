package com.redha.tourguide_modulith.user;

import org.springframework.context.ApplicationEvent;

public class UsersInitializedEvent extends ApplicationEvent {
    public UsersInitializedEvent(Object source) {
        super(source);
    }
}