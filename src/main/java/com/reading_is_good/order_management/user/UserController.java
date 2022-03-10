package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.authentication.AuthenticationFailException;
import com.reading_is_good.order_management.authentication.AuthenticationService;
import com.reading_is_good.order_management.common.dto.ResponseDto;
import com.reading_is_good.order_management.common.exception.CustomException;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.reading_is_good.order_management.common.MessageStrings.EMAIL_ALREADY_EXISTS;
import static com.reading_is_good.order_management.common.MessageStrings.USER_CREATED;
import static org.slf4j.LoggerFactory.getLogger;

@RequestMapping("/users")
@RestController
public class UserController {
    private static final Logger log = getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    ResponseEntity<ResponseDto> signup(@RequestBody UserSignupDto userSignupDto) {
        try {
            userService.create(userSignupDto);
            return new ResponseEntity<>(new ResponseDto(true, USER_CREATED), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            log.error("Error occurred when creating user : ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EMAIL_ALREADY_EXISTS);
        } catch (CustomException e) {
            log.error("Error occurred when creating user : ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{id}/orders")
    ResponseEntity<ResponseDto> fetchOrders(@RequestParam int page,
                                            @RequestParam int size,
                                            @RequestParam String token,
                                            @PathVariable int id) {
        try {
            authenticationService.authenticate(token);
            userService.fetchOrders(id, page, size);
        } catch (UserNotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AuthenticationFailException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }
}
