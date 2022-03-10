package com.reading_is_good.order_management.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;

    public AuthenticationServiceImpl(AuthenticationTokenRepository authenticationTokenRepository) {
        this.authenticationTokenRepository = authenticationTokenRepository;
    }

    public void saveConfirmationToken(AuthenticationToken authenticationToken) {
        authenticationTokenRepository.save(authenticationToken);
    }
}