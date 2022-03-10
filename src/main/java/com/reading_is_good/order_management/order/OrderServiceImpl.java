package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.book.Book;
import com.reading_is_good.order_management.book.BookRepository;
import com.reading_is_good.order_management.user.User;
import com.reading_is_good.order_management.user.UserNotFound;
import com.reading_is_good.order_management.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reading_is_good.order_management.common.MessageStrings.USER_NOT_FOUND;
import static com.reading_is_good.order_management.order.OrderStatus.*;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, BookRepository bookRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public OrderResponseDto create(OrderDto orderDto) throws UserNotFound {
        Order order = new Order();

        Optional<User> userOptional = userRepository.findById(orderDto.getUserId());

        if (userOptional.isEmpty()) {
            throw new UserNotFound(USER_NOT_FOUND);
        }

        User user = userOptional.get();
        order.setUser(user);

        orderRepository.save(order);

        return saveOrderItem(orderDto, order);
    }

    public List<Order> fetchOrdersByUserId(User user, int pageLimit, int size) {
        List<Order> paginatedOrders = new ArrayList<>();
        paginatedOrderRecursion(user, pageLimit, size, paginatedOrders);
        return paginatedOrders;
    }

    private void paginatedOrderRecursion(User user, int pageLimit, int size, List<Order> paginatedOrders) {
        Pageable paging = PageRequest.of(pageLimit, size);
        List<Order> orders = orderRepository.findByUser(user, paging);
        if(orders != null && orders.size() > 0) {
            pageLimit += 1;
            paginatedOrders.addAll(orders);
            this.paginatedOrderRecursion(user, pageLimit, size, paginatedOrders);
        }
    }

    private OrderResponseDto saveOrderItem(OrderDto orderDto, Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        List<OrderItemResponseDto> orderItemResponseDtoList = orderResponseDto.getOrderItemResponseDtoList();

        for (OrderItemDto orderItemDto : orderDto.getOrderItem()) {
            Optional<Book> bookOptional = bookRepository.findById(orderItemDto.getBookId());

            if (bookOptional.isEmpty()) {
                addOrderItemDtoToOrderItemResponseDtoList(orderItemResponseDtoList, BOOK_NOT_FOUND_FOR_GIVEN_BOOK_ID, null, orderItemDto);
                continue;
            }

            Book book = bookOptional.get();

            if (book.getQuantity() <= 0) {
                addOrderItemDtoToOrderItemResponseDtoList(orderItemResponseDtoList, OUT_OF_STOCK, book, orderItemDto);
                continue;
            }
            int quantityLeft = getQuantityLeft(book, orderItemDto);

            if (quantityLeft < 0) {
                addOrderItemDtoToOrderItemResponseDtoList(orderItemResponseDtoList, AVAILABLE_STOCK, book, orderItemDto);
                continue;
            }

            book.setQuantity(quantityLeft);

            OrderItem orderItem = new OrderItem();
            double totalCost = getTotalCost(orderItemDto, book);

            orderItem.setPrice(totalCost);
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(orderItemDto.getQuantity());

            orderItemRepository.save(orderItem);

            addOrderItemDtoToOrderItemResponseDtoList(orderItemResponseDtoList, ORDERED, book, orderItemDto);
        }
        orderResponseDto.setOrderItemResponseDtoList(orderItemResponseDtoList);
        return orderResponseDto;
    }

    private double getTotalCost(OrderItemDto orderItemDto, Book book) {
        return book.getPrice() * orderItemDto.getQuantity();
    }

    private int getQuantityLeft(Book book, OrderItemDto orderItemDto) {
        return book.getQuantity() - orderItemDto.getQuantity();
    }

    private void addOrderItemDtoToOrderItemResponseDtoList(List<OrderItemResponseDto> orderItemResponseDtoList,
                                                           OrderStatus orderStatus, Book book,
                                                           OrderItemDto orderItemDto) {
        OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto();

        if (orderStatus.equals(AVAILABLE_STOCK))
            orderItemResponseDto.setQuantity(book.getQuantity());
        else
            orderItemResponseDto.setQuantity(orderItemDto.getQuantity());

        orderItemResponseDto.setOrderStatus(orderStatus);
        orderItemResponseDto.setBookId(orderItemDto.getBookId());
        orderItemResponseDtoList.add(orderItemResponseDto);
    }
}