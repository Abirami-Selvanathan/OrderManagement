package com.reading_is_good.order_management.book;

import com.reading_is_good.order_management.common.dto.ResponseDto;
import com.reading_is_good.order_management.order.OrderController;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.reading_is_good.order_management.common.MessageStrings.*;
import static org.slf4j.LoggerFactory.getLogger;

@RequestMapping("/books")
@RestController
public class BookController {

    private static final Logger log = getLogger(BookController.class);

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
            log.error("Exception occurred due to duplicate book name", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BOOK_ALREADY_EXISTS);
        }
    }

    @PutMapping("/{id}/quantity/{quantity}")
    public ResponseEntity<ResponseDto> updateQuantity(@PathVariable int id, @PathVariable int quantity) {
        try {
            bookService.updateQuantity(id, quantity);
            return new ResponseEntity<>(new ResponseDto(true, QUANTITY_UPDATED_SUCCESSFULLY),
                    HttpStatus.OK);
        } catch (BookNotFound e) {
            log.error("Exception occurred due to invalid book id", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
