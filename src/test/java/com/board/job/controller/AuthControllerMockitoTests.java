package com.board.job.controller;

import com.board.job.model.dto.login.LoginRequest;
import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.entity.Role;
import com.board.job.model.entity.User;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import com.board.job.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static com.board.job.helper.HelperForTests.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class AuthControllerMockitoTests {
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper mapper;
    @Mock
    private RoleService roleService;
    @InjectMocks
    private AuthController authController;

    @Test
    public void test_Valid_Login() {
        String email = "violet@mail.co";
        String password = "6666";
        User user = new User();
        user.setEmail(email);
        user.setPassword("kdfdpkfpeskfpksfd");

        when(userService.readByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtils.generateTokenFromEmail(email)).thenReturn("testToken");

        String token = authController.login(LoginRequest.of(email, password));

        assertEquals("testToken", token);
        verify(jwtUtils, times(1)).generateTokenFromEmail(email);
    }

    @Test
    public void test_Invalid_Password_Login() {
        String email = "nikole@mail.co";
        String password = "1234";
        User user = new User();
        user.setEmail(email);
        user.setPassword("FEFJGkefflMFKNjHILU@(43ENJDjbj");

        when(userService.readByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authController.login(LoginRequest.of(email, password)));

        verify(jwtUtils, times(0)).generateTokenFromEmail(email);
    }

    @Test
    public void test_Register() {
        UserCreateRequest userCreate = createUser("Maks", "Korniev", "maks@mail.co", "1234");
        User user = new User();
        user.setFirstName("Maks");
        user.setLastName("Korniev");
        user.setEmail("maks@mail.co");
        user.setPassword("1234");
        Role role = new Role("TEST");

        when(roleService.readByName("USER")).thenReturn(role);
        when(mapper.getUserFromUserCreate(userCreate)).thenReturn(user);
        when(userService.create(eq(user), eq(Set.of(role)))).thenReturn(user);

        authController.create(userCreate);

        verify(mapper, times(1)).getUserResponseFromUser(user);
    }
}
