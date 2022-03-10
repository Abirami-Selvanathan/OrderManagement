package com.reading_is_good.order_management.statistics;

import com.reading_is_good.order_management.order.Order;
import com.reading_is_good.order_management.order.OrderItem;
import com.reading_is_good.order_management.order.OrderRepository;
import com.reading_is_good.order_management.user.User;
import com.reading_is_good.order_management.user.UserNotFound;
import com.reading_is_good.order_management.user.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.reading_is_good.order_management.common.MessageStrings.USER_NOT_FOUND;
import static java.time.ZoneId.systemDefault;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StatisticsServiceTest {

    OrderRepository orderRepository = mock(OrderRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    StatisticsServiceImpl statisticsService = new StatisticsServiceImpl(orderRepository, userRepository);

    @Test
    public void shouldThrowUserNotFoundWhenUserDoesNotExists() throws UserNotFound {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(empty());

        assertThrows(UserNotFound.class, () -> statisticsService.fetch((int) userId),
                USER_NOT_FOUND);
    }

    @Test
    public void shouldReturnEmptyListWhenUserHasNoOrders() throws UserNotFound {
        long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(of(user));
        when(orderRepository.findAllByUserOrderByCreatedDateDesc(any())).thenReturn(null);

        List<StatisticsResponseDto> statisticsResponseDtos = statisticsService.fetch((int) userId);

        assertEquals(0, statisticsResponseDtos.size());
    }

    @Test
    public void shouldReturnStatisticsResponseDtoWhenUserHasOrders() throws UserNotFound {
        long userId = 1L;
        User user = new User();
        LinkedList<OrderItem> orderItems = buildOrderItems();
        Order order = buildOrder(user, orderItems);
        List<Order> orders = new LinkedList<>();
        orders.add(order);

        when(userRepository.findById(userId)).thenReturn(of(user));
        when(orderRepository.findAllByUserOrderByCreatedDateDesc(any())).thenReturn(orders);

        List<StatisticsResponseDto> statisticsResponseDtos = statisticsService.fetch((int) userId);

        StatisticsResponseDto statisticsResponseDto = statisticsResponseDtos.get(0);
        String expectedMonth = getMonth(order.getCreatedDate());
        assertEquals(1, statisticsResponseDto.getOrderCount());
        assertEquals(1, statisticsResponseDto.getBookCount());
        assertEquals(100, statisticsResponseDto.getTotalPrice());
        assertEquals(expectedMonth, statisticsResponseDto.getMonth());
    }

    private Order buildOrder(User user, LinkedList<OrderItem> orderItems) {
        Order order = new Order();
        order.setUser(user);
        order.onCreate();
        order.setOrderItems(orderItems);
        return order;
    }

    private LinkedList<OrderItem> buildOrderItems() {
        LinkedList<OrderItem> orderItems = new LinkedList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setPrice(100);
        orderItems.add(orderItem);
        return orderItems;
    }

    private String getMonth(Date created) {
        LocalDate localDate = created.toInstant().atZone(systemDefault()).toLocalDate();
        int month = localDate.getMonthValue() - 1;
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[month];
    }
}