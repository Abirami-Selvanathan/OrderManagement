package com.reading_is_good.order_management.statistics;

import com.reading_is_good.order_management.authentication.AuthenticationFailException;
import com.reading_is_good.order_management.authentication.AuthenticationService;
import com.reading_is_good.order_management.order.OrderController;
import com.reading_is_good.order_management.user.UserNotFound;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private static final Logger log = getLogger(StatisticsController.class);
    private final StatisticsService statisticsService;
    private final AuthenticationService authenticationService;

    public StatisticsController(StatisticsService statisticsService, AuthenticationService authenticationService) {
        this.statisticsService = statisticsService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public ResponseEntity<List<StatisticsResponseDto>> fetch(@RequestParam String token, @RequestParam int userId) {
        try {
            authenticationService.authenticate(token);
            List<StatisticsResponseDto> statisticsResponseDtos = statisticsService.fetch(userId);
            return new ResponseEntity<>(statisticsResponseDtos, HttpStatus.OK);
        } catch (UserNotFound e) {
            log.error("Exception occurred due to invalid user id", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationFailException e) {
            log.error("Exception occurred due to invalid token", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}

