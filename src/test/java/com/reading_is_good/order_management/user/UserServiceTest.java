package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.authentication.AuthenticationServiceImpl;
import com.reading_is_good.order_management.common.exception.CustomException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    UserRepository userRepository = mock(UserRepository.class);

    AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);

    UserService userService = new UserServiceImpl(userRepository, authenticationService);

    @Test
    public void shouldCreateUserWhenUserRequestDtoIsGiven() throws CustomException {
        User user = new User();
        UserSignupDto userSignupDto = new UserSignupDto();
        userSignupDto.setFirstName("First name");
        userSignupDto.setLastName("Last name");
        userSignupDto.setEmail("test@test.com");
        userSignupDto.setPassword("password");
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(authenticationService).saveConfirmationToken(any());

        userService.create(userSignupDto);

        verify(userRepository, times(1)).save(any());
        verify(authenticationService, times(1)).saveConfirmationToken(any());
    }
}