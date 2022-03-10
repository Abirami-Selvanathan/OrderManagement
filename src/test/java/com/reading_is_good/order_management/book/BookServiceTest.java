package com.reading_is_good.order_management.book;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BookServiceTest {
    BookRepository bookRepository = mock(BookRepository.class);

    BookService bookService = new BookServiceImpl(bookRepository);

    @Test
    public void shouldCreateBookWhenBookDtoIsGiven() {
        Book book = new Book();
        BookDto bookDto = buildBookDto();

        when(bookRepository.save(any())).thenReturn(book);

        bookService.create(bookDto);

        verify(bookRepository, times(1)).save(any());
    }

    private BookDto buildBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setName("Book 1");
        bookDto.setQuantity(10);
        bookDto.setPrice(100);
        return bookDto;
    }
}