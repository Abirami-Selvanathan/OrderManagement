package com.reading_is_good.order_management.user;

import com.reading_is_good.order_management.OrderManagementApplication;
import com.reading_is_good.order_management.authentication.AuthenticationTokenRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.reading_is_good.order_management.common.MessageStrings.USER_CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = OrderManagementApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationTokenRepository authenticationTokenRepository;

    @Test
    public void returnsCreatedWhenUserIsCreated() throws Exception {
        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"First name\",\"lastName\":\"Last name\",\"email\":\"user@test.com\"," +
                                "\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(USER_CREATED));

        authenticationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void returnsBadRequestIfEmailAlreadyExists() throws Exception {
        User user = new User();
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setEmail("existingUser@test.com");
        user.setPassword("password");
        userRepository.save(user);

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"First name\",\"lastName\":\"Last name\",\"email\":" +
                                "\"existingUser@test.com\",\"password\":\"password\"}"))
                .andExpect(status().isBadRequest());

        authenticationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void returnsUnAuthorizedWhenInvalidTokenIsGiven() throws Exception {
        mvc.perform(get("/users/1/orders?token=token&page=0&size=2"))
                .andExpect(status().isUnauthorized());
    }
}