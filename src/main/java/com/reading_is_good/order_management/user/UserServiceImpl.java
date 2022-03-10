package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.authentication.AuthenticationServiceImpl;
import com.reading_is_good.order_management.authentication.AuthenticationToken;
import com.reading_is_good.order_management.common.exception.CustomException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.reading_is_good.order_management.common.MessageStrings.SOMETHING_WENT_WRONG;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = getLogger(UserServiceImpl.class);
    private static final String MD_5 = "MD5";

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationServiceImpl authenticationService;

    public UserServiceImpl(UserRepository userRepository, AuthenticationServiceImpl authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public void create(UserSignupDto userSignupDto) throws CustomException {
        String encryptedPassword = hashPassword(userSignupDto.getPassword());

        User user = userSignupDto.toUser();
        user.setPassword(encryptedPassword);
        User createdUser = userRepository.save(user);

        AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
        authenticationService.saveConfirmationToken(authenticationToken);
    }

    String hashPassword(String password) throws CustomException {
        try {
            MessageDigest md = MessageDigest.getInstance(MD_5);
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("Password hashing failed : ", e);
            throw new CustomException(SOMETHING_WENT_WRONG);
        }
    }
}
