package com.reading_is_good.order_management.authentication;

import com.reading_is_good.order_management.common.exception.CustomException;
import com.reading_is_good.order_management.user.User;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthenticationServiceTest {

    AuthenticationTokenRepository authenticationTokenRepository = mock(AuthenticationTokenRepository.class);

    AuthenticationService authenticationService = new AuthenticationServiceImpl(authenticationTokenRepository);

    @Test
    public void shouldCreateUserWhenUserRequestDtoIsGiven() throws CustomException {
        User user = new User();
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setUser(user);
        authenticationToken.setToken("token");

        authenticationService.saveConfirmationToken(authenticationToken);

        verify(authenticationTokenRepository, times(1)).save(authenticationToken);
    }
}