package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.user.UserNotFound;

public interface OrderService {
    OrderResponseDto create(OrderDto orderDto) throws UserNotFound;
}
