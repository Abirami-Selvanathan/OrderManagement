package com.reading_is_good.order_management.order;

import java.util.List;

public class OrderDto {

    private Long customerId;

    private List<OrderItemDto> orderItem;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemDto> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItemDto> orderItem) {
        this.orderItem = orderItem;
    }
}
