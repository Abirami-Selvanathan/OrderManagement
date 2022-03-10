package com.reading_is_good.order_management.book;

import com.reading_is_good.order_management.OrderManagementApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = OrderManagementApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations= "classpath:application.properties")
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void returnsCreatedWhenBookIsCreated() throws Exception {
        mvc.perform(post("/books")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Book 1\",\"quantity\":10,\"price\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Book created successfully!"));

        bookRepository.deleteAll();
    }

    @Test
    public void returnsBadRequestIfBookAlreadyExists() throws Exception {
        Book book = new Book();
        book.setName("Existing Book 1");
        book.setQuantity(10);
        book.setPrice(100);
        bookRepository.save(book);

        mvc.perform(post("/books")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Existing Book 1\",\"quantity\":10,\"price\":100}"))
                .andExpect(status().isBadRequest());

        bookRepository.deleteById(book.getId());
    }
}