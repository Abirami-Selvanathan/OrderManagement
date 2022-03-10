package com.reading_is_good.order_management.book;

public class BookNotFound extends Throwable {
    public BookNotFound(String message) {
        super(message);
    }
}