package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private final UserRepository userRep = mock(UserRepository.class);
    private final CartRepository cartRep = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRep);
        TestUtils.injectObjects(userController, "cartRepository", cartRep);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode("testPassword1")).thenReturn("PasswordIsHashed");
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("testUser1");
        req.setPassword("testPassword1");
        req.setConfirmPassword("testPassword1");

        ResponseEntity<User> response = userController.createUser(req);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUser1", user.getUsername());
        assertEquals("PasswordIsHashed", user.getPassword());
    }

    @Test
    public void checkFindByUserName() {
        User user = new User();
        user.setUsername("testUser1");
        user.setPassword("testPassword1");
        when(userRep.findByUsername("testUser1")).thenReturn(user);

        final ResponseEntity<User> username = userController.findByUserName("testUser1");
        assertNotNull(username);
        assertNotNull(username);
        assertEquals (200, username.getStatusCodeValue());
        assertEquals("testUser1", username.getBody().getUsername());
    }

    @Test
    public void checkPasswordLength() {
        // pass1 is less than 7 characters
        when(encoder.encode("pass1")).thenReturn("passwordIsHashed");
        CreateUserRequest req = new CreateUserRequest();

        req.setUsername("testUser2");
        req.setPassword("pass1");  // 8 characters
        req.setConfirmPassword("pass1");

        final ResponseEntity<User> userResp = userController.createUser(req);
        assertNotNull(userResp);
        assertEquals(400, userResp.getStatusCodeValue());
    }

    @Test
    public void checkUnmatchedPasswords() {
        when(encoder.encode("testPass-1")).thenReturn("passwordIsHashed");
        CreateUserRequest req = new CreateUserRequest();

        req.setUsername("testUser2");
        req.setPassword("testPass-1");
        req.setConfirmPassword("testPassword-2");

        final ResponseEntity<User> userResp = userController.createUser(req);
        assertNotNull(userResp);
        assertNotEquals(200, userResp.getStatusCodeValue());
    }
}
