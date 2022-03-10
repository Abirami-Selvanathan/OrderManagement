package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.authentication.AuthenticationFailException;
import com.reading_is_good.order_management.authentication.AuthenticationService;
import com.reading_is_good.order_management.user.UserNotFound;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = getLogger(OrderController.class);
    private final OrderService orderService;
    private final AuthenticationService authenticationService;

    public OrderController(OrderService orderService, AuthenticationService authenticationService) {
        this.orderService = orderService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestParam String token, @RequestBody OrderDto orderDto) {
        try {
            authenticationService.authenticate(token);
            return new ResponseEntity<>(orderService.create(orderDto), HttpStatus.CREATED);
        } catch (UserNotFound e) {
            log.error("Exception occurred because customer does not exists");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationFailException e) {
            log.error("Exception occurred due to invalid user");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}