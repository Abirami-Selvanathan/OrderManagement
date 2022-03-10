package com.reading_is_good.order_management.authentication;

import com.reading_is_good.order_management.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Integer> {
    AuthenticationToken findAuthenticationTokenByUser(User user);
    AuthenticationToken findAuthenticationTokenByToken(String token);
}
