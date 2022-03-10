package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.OrderManagementApplication;
import com.reading_is_good.order_management.authentication.AuthenticationToken;
import com.reading_is_good.order_management.authentication.AuthenticationTokenRepository;
import com.reading_is_good.order_management.book.Book;
import com.reading_is_good.order_management.book.BookRepository;
import com.reading_is_good.order_management.user.User;
import com.reading_is_good.order_management.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = OrderManagementApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationTokenRepository authenticationTokenRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void returnsCreatedWhenValidOrderDtoIsGiven() throws Exception {
        User user = persistUser();
        Book book = persistBook();
        AuthenticationToken authenticationToken = persistAuthorizationToken(user);

        String contentJson = "{\"customerId\":" + user.getId() + ",\"orderItem\":" +
                "[{\"bookId\":" + book.getId() + ",\"quantity\":1}]}";

        mvc.perform(post("/orders?token="+authenticationToken.getToken())
                        .contentType(APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isCreated());

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        bookRepository.deleteById(book.getId());
        authenticationTokenRepository.deleteById(authenticationToken.getId());
        userRepository.deleteById(user.getId());
    }

    @Test
    public void returnsUnauthorizedWhenUserDoesNotHaveValidAuthToken() throws Exception {
        String contentJson = "{\"customerId\":" + new Random().nextLong() + ",\"orderItem\":" +
                "[{\"bookId\":" + new Random().nextLong() + ",\"quantity\":1}]}";

        mvc.perform(post("/orders?token=token")
                        .contentType(APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isUnauthorized());
    }

    private AuthenticationToken persistAuthorizationToken(User user) {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setToken("token");
        authenticationToken.setUser(user);
        authenticationTokenRepository.save(authenticationToken);
        return authenticationToken;
    }

    private User persistUser() {
        User user = new User();
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setEmail("test@test.com");
        user.setPassword("password");
        userRepository.save(user);
        return user;
    }

    private Book persistBook() {
        Book book = new Book();
        book.setName("Book 1");
        book.setPrice(100);
        book.setQuantity(10);
        bookRepository.save(book);
        return book;
    }
}