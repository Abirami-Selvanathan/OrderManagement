package com.reading_is_good.order_management.authentication;

import com.reading_is_good.order_management.common.MessageStrings;
import com.reading_is_good.order_management.user.User;
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

    public User getUser(String token) {
        AuthenticationToken authenticationToken = authenticationTokenRepository.findAuthenticationTokenByToken(token);
        if (authenticationToken != null) {
            if (authenticationToken.getUser() != null) {
                return authenticationToken.getUser();
            }
        }
        return null;
    }

    public void authenticate(String token) throws AuthenticationFailException {
        if (token == null) {
            throw new AuthenticationFailException(MessageStrings.AUTH_TOKEN_NOT_PRESENT);
        }
        User user = getUser(token);
        if (user == null) {
            throw new AuthenticationFailException(MessageStrings.INVALID_AUTH_TOKEN);
        }
    }
}