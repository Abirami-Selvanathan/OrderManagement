package com.reading_is_good.order_management.book;

import com.reading_is_good.order_management.common.dto.ResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.reading_is_good.order_management.common.MessageStrings.BOOK_ALREADY_EXISTS;
import static com.reading_is_good.order_management.common.MessageStrings.BOOK_CREATED_SUCCESSFULLY;

@RequestMapping("/books")
@RestController
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> create(@RequestBody BookDto bookDto) {
        try {
            bookService.create(bookDto);
            return new ResponseEntity<>(new ResponseDto(true, BOOK_CREATED_SUCCESSFULLY),
                    HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BOOK_ALREADY_EXISTS);
        }
    }
}
