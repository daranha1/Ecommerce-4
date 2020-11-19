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

    ////////////// Test-1 : Create user successfully ///////////////////////////
    @Test
    public void create_user_happy_path() {
        when(encoder.encode("testPassword-1")).thenReturn("PasswordIsHashed");
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("testUser-1");
        req.setPassword("testPassword-1");
        req.setConfirmPassword("testPassword-1");

        ResponseEntity<User> response = userController.createUser(req);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUser-1", user.getUsername());
        assertEquals("PasswordIsHashed", user.getPassword());
    }

    /////////// Test 2 : Find user by username sucessfully ////////////////
    @Test
    public void check_Find_By_UserName() {
        User user = new User();
        user.setUsername("testUser-2");
        user.setPassword("testPassword-2");
        when(userRep.findByUsername("testUser-2")).thenReturn(user);

        final ResponseEntity<User> username = userController.findByUserName("testUser-2");
        assertNotNull(username);
        assertNotNull(username);
        assertEquals (200, username.getStatusCodeValue());
        assertEquals("testUser-2", username.getBody().getUsername());
    }
    //////////// Test 3 : User not created due to password length < 7 characters //////
    @Test
    public void check_Password_Length() {
        // pass1 is less than 7 characters
        when(encoder.encode("pass-1")).thenReturn("passwordIsHashed");
        CreateUserRequest req = new CreateUserRequest();

        req.setUsername("testUser-3");
        req.setPassword("pass-3");  // less than 7 characters
        req.setConfirmPassword("pass-3");

        final ResponseEntity<User> userResp = userController.createUser(req);
        assertNotNull(userResp);
        assertEquals(400, userResp.getStatusCodeValue());
    }

    ////////// Test 4 : User not created due to unmatched passwords ///////////
    @Test
    public void check_Unmatched_Passwords() {
        when(encoder.encode("testPass-4A")).thenReturn("passwordIsHashed");
        CreateUserRequest req = new CreateUserRequest();

        req.setUsername("testUser-4");
        req.setPassword("testPass-4A");
        req.setConfirmPassword("testPassword-4B");

        final ResponseEntity<User> userResp = userController.createUser(req);
        assertNotNull(userResp);
        assertNotEquals(200, userResp.getStatusCodeValue());
    }

    //////////////// Test 5 : User found by the right userId //////////////////
    @Test
    public void check_Find_By_UserId() {
        User user = new User();
        user.setId(3);
        user.setUsername("test-5");
        user.setPassword("testPass-5");

        when(userRep.findById((long) 3)).thenReturn(java.util.Optional.of(user));
        final ResponseEntity<User> locateById = userController.findById(3L);

        assertNotNull(locateById);
        assertEquals(200, locateById.getStatusCodeValue());
        assertEquals(3, locateById.getBody().getId());
    }
}
