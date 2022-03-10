package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.common.dto.ResponseDto;
import com.reading_is_good.order_management.common.exception.CustomException;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.reading_is_good.order_management.common.MessageStrings.EMAIL_ALREADY_EXISTS;
import static com.reading_is_good.order_management.common.MessageStrings.USER_CREATED;
import static org.slf4j.LoggerFactory.getLogger;

@RequestMapping("/users")
@RestController
public class UserController {
    private static final Logger log = getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
