package com.reading_is_good.order_management.order;

import java.util.LinkedList;
import java.util.List;

public class OrderResponseDto {
    private List<OrderItemResponseDto> orderItemResponseDtoList = new LinkedList<>();

    public List<OrderItemResponseDto> getOrderItemResponseDtoList() {
        return orderItemResponseDtoList;
    }

    public void setOrderItemResponseDtoList(List<OrderItemResponseDto> orderItemResponseDtoList) {
        this.orderItemResponseDtoList = orderItemResponseDtoList;
    }
}
