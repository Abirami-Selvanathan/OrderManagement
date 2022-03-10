package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.common.exception.CustomException;

public interface UserService {
    void create(UserSignupDto userSignupDto) throws CustomException;
}
