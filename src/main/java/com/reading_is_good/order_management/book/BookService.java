package com.reading_is_good.order_management.book;

public interface BookService {
    void create(BookDto bookDto);
    void updateQuantity(int id, int quantity) throws BookNotFound;
}
