package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.book.Book;
import com.reading_is_good.order_management.book.BookRepository;
import com.reading_is_good.order_management.user.User;
import com.reading_is_good.order_management.user.UserNotFound;
import com.reading_is_good.order_management.user.UserRepository;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

import static com.reading_is_good.order_management.common.MessageStrings.USER_NOT_FOUND;
import static com.reading_is_good.order_management.order.OrderStatus.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderServiceImplTest {
    OrderRepository orderRepository = mock(OrderRepository.class);
    OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
    BookRepository bookRepository = mock(BookRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    OrderService orderService = new OrderServiceImpl(orderRepository, orderItemRepository, bookRepository, userRepository);

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserDoesNotExists() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(empty());
        assertThrows(UserNotFound.class, () -> orderService.create(new OrderDto()), USER_NOT_FOUND);
    }

    @Test
    public void shouldReturnOrderResponseDtoWithOutOfStockStatusWhenBookQuantityIsZero() throws UserNotFound {
        long userId = 1L;
        long bookId = 1L;
        int requiredQuantity = 1;
        int availableQuantity = 0;
        OrderDto orderDto = buildOrderDto(userId, bookId, requiredQuantity);

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(of(user));

        Book book = buildBook(bookId, availableQuantity);
        when(bookRepository.findById(bookId)).thenReturn(of(book));
        when(orderRepository.save(any())).thenReturn(new Order());

        when(orderItemRepository.save(any())).thenReturn(new OrderItem());

        OrderResponseDto orderResponseDto = orderService.create(orderDto);

        OrderItemResponseDto orderItemResponseDto = orderResponseDto.getOrderItemResponseDtoList().get(0);
        assertEquals(OUT_OF_STOCK, orderItemResponseDto.getOrderStatus());
        assertEquals(requiredQuantity, orderItemResponseDto.getQuantity());
        assertEquals(bookId, orderItemResponseDto.getBookId());
    }

    @Test
    public void shouldReturnBookNotFoundStatusWhenBookIsNotPresent() throws UserNotFound {
        long userId = 1L;
        long bookId = 1L;
        int requiredQuantity = 1;
        OrderDto orderDto = buildOrderDto(userId, bookId, requiredQuantity);

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(of(user));

        when(bookRepository.findById(bookId)).thenReturn(empty());
        when(orderRepository.save(any())).thenReturn(new Order());

        when(orderItemRepository.save(any())).thenReturn(new OrderItem());

        OrderResponseDto orderResponseDto = orderService.create(orderDto);

        OrderItemResponseDto orderItemResponseDto = orderResponseDto.getOrderItemResponseDtoList().get(0);
        assertEquals(BOOK_NOT_FOUND_FOR_GIVEN_BOOK_ID, orderItemResponseDto.getOrderStatus());
        assertEquals(requiredQuantity, orderItemResponseDto.getQuantity());
        assertEquals(bookId, orderItemResponseDto.getBookId());
    }

    @Test
    public void shouldReturnAvailableStockStatusAndAvailableQuantityWhenOrderQuantityIsMoreThanAvailable() throws UserNotFound {
        long userId = 1L;
        long bookId = 1L;
        int requiredQuantity = 11;
        int availableQuantity = 10;
        OrderDto orderDto = buildOrderDto(userId, bookId, requiredQuantity);

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(of(user));

        Book book = buildBook(bookId, availableQuantity);
        when(bookRepository.findById(bookId)).thenReturn(of(book));
        when(orderRepository.save(any())).thenReturn(new Order());

        when(orderItemRepository.save(any())).thenReturn(new OrderItem());

        OrderResponseDto orderResponseDto = orderService.create(orderDto);

        OrderItemResponseDto orderItemResponseDto = orderResponseDto.getOrderItemResponseDtoList().get(0);
        assertEquals(AVAILABLE_STOCK, orderItemResponseDto.getOrderStatus());
        assertEquals(availableQuantity, orderItemResponseDto.getQuantity());
        assertEquals(bookId, orderItemResponseDto.getBookId());
    }

    @Test
    public void shouldReturnOrderedStatusWhenUserAndBookIsAvailable() throws UserNotFound {
        long userId = 1L;
        long bookId = 1L;
        int requiredQuantity = 1;
        int availableQuantity = 10;
        OrderDto orderDto = buildOrderDto(userId, bookId, requiredQuantity);
        Book book = buildBook(bookId, availableQuantity);
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(of(user));
        when(bookRepository.findById(bookId)).thenReturn(of(book));
        when(orderRepository.save(any())).thenReturn(new Order());
        when(orderItemRepository.save(any())).thenReturn(new OrderItem());

        OrderResponseDto orderResponseDto = orderService.create(orderDto);

        OrderItemResponseDto orderItemResponseDto = orderResponseDto.getOrderItemResponseDtoList().get(0);
        assertEquals(ORDERED, orderItemResponseDto.getOrderStatus());
        assertEquals(requiredQuantity, orderItemResponseDto.getQuantity());
        assertEquals(bookId, orderItemResponseDto.getBookId());
    }

    private OrderDto buildOrderDto(long userId, long bookId, int requiredQuantity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userId);
        OrderItemDto orderItemDto = buildOrderItemDto(bookId, requiredQuantity);

        List<OrderItemDto> orderItemDtoList = new LinkedList<>();
        orderItemDtoList.add(orderItemDto);

        orderDto.setOrderItem(orderItemDtoList);
        return orderDto;
    }

    private OrderItemDto buildOrderItemDto(long bookId, int requiredQuantity) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setBookId(bookId);
        orderItemDto.setQuantity(requiredQuantity);
        return orderItemDto;
    }

    private Book buildBook(long bookId, int availableQuantity) {
        Book book = new Book();
        book.setId(bookId);
        book.setQuantity(availableQuantity);
        return book;
    }
}