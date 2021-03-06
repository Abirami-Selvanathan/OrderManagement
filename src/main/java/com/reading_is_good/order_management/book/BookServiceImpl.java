package com.reading_is_good.order_management.book;

import org.springframework.stereotype.Service;

import static com.reading_is_good.order_management.common.MessageStrings.BOOK_NOT_FOUND;

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

    public void updateQuantity(int id, int quantity) throws BookNotFound {
        Book book = bookRepository.findById((long) id).orElseThrow(() -> new BookNotFound(BOOK_NOT_FOUND));
        book.setQuantity(quantity);
        bookRepository.save(book);
    }
}
