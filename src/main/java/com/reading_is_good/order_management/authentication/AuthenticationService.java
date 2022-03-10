package com.reading_is_good.order_management.authentication;

public interface AuthenticationService {
    void saveConfirmationToken(AuthenticationToken authenticationToken);
    void authenticate(String token) throws AuthenticationFailException;
}
