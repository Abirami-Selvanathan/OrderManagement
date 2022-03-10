package com.reading_is_good.order_management.statistics;

import com.reading_is_good.order_management.OrderManagementApplication;
import com.reading_is_good.order_management.authentication.AuthenticationToken;
import com.reading_is_good.order_management.authentication.AuthenticationTokenRepository;
import com.reading_is_good.order_management.book.Book;
import com.reading_is_good.order_management.book.BookRepository;
import com.reading_is_good.order_management.order.Order;
import com.reading_is_good.order_management.order.OrderItem;
import com.reading_is_good.order_management.order.OrderItemRepository;
import com.reading_is_good.order_management.order.OrderRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = OrderManagementApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class StatisticsControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AuthenticationTokenRepository authenticationTokenRepository;

    @Test
    public void returnsNotFoundWhenUserDoesNotExists() throws Exception {
        mvc.perform(get("/statistics?userId=" + new Random().nextInt() +"&token=token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void returnsStatisticsResponseDtoWhenUserIdIsGiven() throws Exception {
        User user = persistUser();
        AuthenticationToken authenticationToken = persistAuthorizationToken(user);
        Book book = persistBook();
        Order order = persistOrder(user);
        OrderItem orderItem = persistOrderItem(book, order);

        mvc.perform(get("/statistics?userId=" + user.getId()+"&token="+authenticationToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderCount").value(1))
                .andExpect(jsonPath("$[0].bookCount").value(1))
                .andExpect(jsonPath("$[0].totalPrice").value(100));

        orderItemRepository.deleteById(orderItem.getId());
        orderRepository.deleteById(order.getId());
        bookRepository.deleteById(book.getId());
        authenticationTokenRepository.deleteById(authenticationToken.getId());
        userRepository.deleteById(user.getId());
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

    private Order persistOrder(User user) {
        Order order = new Order();
        order.setUser(user);
        orderRepository.save(order);
        return order;
    }

    private OrderItem persistOrderItem(Book book, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(book);
        orderItem.setPrice(100);
        orderItem.setQuantity(1);
        orderItemRepository.save(orderItem);
        return orderItem;
    }
}