package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.authentication.AuthenticationServiceImpl;
import com.reading_is_good.order_management.common.exception.CustomException;
import com.reading_is_good.order_management.order.Order;
import com.reading_is_good.order_management.order.OrderService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.reading_is_good.order_management.common.MessageStrings.USER_NOT_FOUND;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    UserRepository userRepository = mock(UserRepository.class);

    AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);

    OrderService orderService = mock(OrderService.class);

    UserService userService = new UserServiceImpl(userRepository, orderService, authenticationService);

    @Test
    public void shouldCreateUserWhenUserRequestDtoIsGiven() throws CustomException {
        User user = new User();
        UserSignupDto userSignupDto = new UserSignupDto();
        userSignupDto.setFirstName("First name");
        userSignupDto.setLastName("Last name");
        userSignupDto.setEmail("test@test.com");
        userSignupDto.setPassword("password");
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(authenticationService).saveConfirmationToken(any());

        userService.create(userSignupDto);

        verify(userRepository, times(1)).save(any());
        verify(authenticationService, times(1)).saveConfirmationToken(any());
    }

    @Test
    public void shouldThrowUserNotFoundWhenUserIdDoesNotExists() {
        int userId = 1;
        int size = 1;
        int page = 1;
        when(userRepository.findById((long) userId)).thenReturn(empty());

        assertThrows(UserNotFound.class, () -> userService.fetchOrders(userId, page, size), USER_NOT_FOUND);
    }

    @Test
    public void shouldReturnOrdersWhenUserIdIsGivenWithPageAndSize() throws UserNotFound {
        int userId = 1;
        int size = 1;
        int page = 1;
        User user = new User();
        when(userRepository.findById((long) userId)).thenReturn(of(user));
        ArrayList<Order> orders = new ArrayList<>();
        when(orderService.fetchOrdersByUserId(user, page, size)).thenReturn(orders);

        List<Order> result = userService.fetchOrders(userId, page, size);

        assertEquals(orders, result);
    }
}