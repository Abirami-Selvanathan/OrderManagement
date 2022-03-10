package com.reading_is_good.order_management.user;

public class UserNotFound extends Throwable {
    public UserNotFound(String message) {
        super(message);
    }
}
