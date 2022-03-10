package com.reading_is_good.order_management.statistics;

import com.reading_is_good.order_management.user.UserNotFound;

import java.util.List;

public interface StatisticsService {
    List<StatisticsResponseDto> fetch(int userId) throws UserNotFound;
}
