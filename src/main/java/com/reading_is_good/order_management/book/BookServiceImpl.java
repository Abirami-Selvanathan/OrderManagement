package com.reading_is_good.order_management.book;

import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void create(BookDto bookDto) {
        Book book = bookDto.toBook();
        bookRepository.save(book);
    }
}
