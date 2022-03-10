package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.user.User;
import com.reading_is_good.order_management.user.UserNotFound;

import java.util.List;

public interface OrderService {
    OrderResponseDto create(OrderDto orderDto) throws UserNotFound;

    List<Order> fetchOrdersByUserId(User user, int pageLimit, int size);

    Order fetchOrderById(int id) throws OrderNotFound;
}
