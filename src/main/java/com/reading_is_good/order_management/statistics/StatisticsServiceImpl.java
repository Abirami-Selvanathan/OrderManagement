package com.reading_is_good.order_management.statistics;

import com.reading_is_good.order_management.order.Order;
import com.reading_is_good.order_management.order.OrderItem;
import com.reading_is_good.order_management.order.OrderRepository;
import com.reading_is_good.order_management.user.User;
import com.reading_is_good.order_management.user.UserNotFound;
import com.reading_is_good.order_management.user.UserRepository;
import org.springframework.stereotype.Service;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.*;

import static com.reading_is_good.order_management.common.MessageStrings.USER_NOT_FOUND;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;

@Service
public class StatisticsServiceImpl implements StatisticsService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public StatisticsServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<StatisticsResponseDto> fetch(int userId) throws UserNotFound {
        User user = userRepository.findById((long) userId).orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        List<Order> orders = orderRepository.findAllByUserOrderByCreatedDateDesc(user);

        if (orders == null || orders.isEmpty()) {
            return emptyList();
        }

        return getStatisticsResponseDtoList(orders);
    }

    private List<StatisticsResponseDto> getStatisticsResponseDtoList(List<Order> orders) {
        Map<String, StatisticsResponseDto> ordersMap = new HashMap<>();
        String[] months = getMonths();

        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            Date created = order.getCreatedDate();
            int month = getMonth(created) - 1;
            int orderCount = orderItems.size();
            int quantity = getTotalQuantityForOrderItem(orderItems);
            double totalPrice = getTotalPriceForOrder(orderItems);
            String monthInWord = months[month];
            StatisticsResponseDto statisticsResponseDto;

            if (ordersMap.containsKey(monthInWord)) {
                statisticsResponseDto = ordersMap.get(monthInWord);
                orderCount = statisticsResponseDto.getOrderCount() + orderCount;
                totalPrice = statisticsResponseDto.getTotalPrice() + getTotalPriceForOrder(orderItems);
                quantity = statisticsResponseDto.getOrderCount() + getTotalQuantityForOrderItem(orderItems);

                setStatisticsResponseDto(statisticsResponseDto, orderCount, totalPrice, quantity, monthInWord);
            } else {
                statisticsResponseDto = new StatisticsResponseDto();
                setStatisticsResponseDto(statisticsResponseDto, orderCount, totalPrice, quantity, monthInWord);
            }
            ordersMap.put(monthInWord, statisticsResponseDto);
        }
        return new ArrayList<>(ordersMap.values());
    }

    private void setStatisticsResponseDto(StatisticsResponseDto statisticsResponseDto, int orderCount, double totalPrice, int quantity, String month) {
        statisticsResponseDto.setOrderCount(orderCount);
        statisticsResponseDto.setTotalPrice(totalPrice);
        statisticsResponseDto.setBookCount(quantity);
        statisticsResponseDto.setMonth(month);
    }

    private int getTotalQuantityForOrderItem(List<OrderItem> order) {
        return order.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    private double getTotalPriceForOrder(List<OrderItem> order) {
        return order.stream().mapToDouble(OrderItem::getPrice).sum();
    }

    private String[] getMonths() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths();
    }

    private int getMonth(Date created) {
        LocalDate localDate = created.toInstant().atZone(systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }
}

