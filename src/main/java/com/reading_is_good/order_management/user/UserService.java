package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.common.exception.CustomException;
import com.reading_is_good.order_management.order.Order;

import java.util.List;

public interface UserService {
    void create(UserSignupDto userSignupDto) throws CustomException;

    List<Order> fetchOrders(int id, int page, int size) throws UserNotFound;
}
