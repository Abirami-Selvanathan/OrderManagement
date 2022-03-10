package com.reading_is_good.order_management.order;

import java.util.List;

public class OrderDto {

    private Long userId;

    private List<OrderItemDto> orderItem;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemDto> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItemDto> orderItem) {
        this.orderItem = orderItem;
    }
}
