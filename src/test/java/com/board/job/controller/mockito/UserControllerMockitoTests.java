package com.board.job.controller.mockito;

import com.board.job.controller.UserController;
import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.dto.user.UserResponse;
import com.board.job.model.dto.user.UserUpdateRequest;
import com.board.job.model.dto.user.UserUpdateRequestWithPassword;
import com.board.job.model.entity.Role;
import com.board.job.model.entity.User;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class UserControllerMockitoTests {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private RoleService roleService;
    @Mock
    private UserMapper mapper;
    @Mock
    private Authentication authentication;

    @Test
    public void test_GetAll() {
        List<User> users = List.of(new User(), new User());

        when(userService.getAll()).thenReturn(users);
        when(mapper.getUserResponseFromUser(new User())).thenReturn(UserResponse.builder().build());
        userController.getAll(authentication);

        verify(userService, times(1)).getAll();
    }

    @Test
    public void test_GetById() {
        long id = 3L;
        when(userService.readById(id)).thenReturn(new User());
        when(mapper.getUserResponseFromUser(new User())).thenReturn(UserResponse.builder().build());
        userController.getById(id, authentication);

        verify(userService, times(1)).readById(id);
    }

    @Test
    public void test_NotFound_GetById() {
        long id = 100L;
        when(userService.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userController.getById(id, authentication));

        verify(userService, times(1)).readById(id);
        verify(mapper, times(0)).getUserResponseFromUser(new User());
    }


    @Test
    public void test_GetByEmail() {
        String email = "email";
        when(userService.readByEmail(email)).thenReturn(new User());
        when(mapper.getUserResponseFromUser(new User())).thenReturn(UserResponse.builder().build());
        userController.getByEmail(email, authentication);

        verify(userService, times(1)).readByEmail(email);
    }

    @Test
    public void test_NotFound_GetByEmail() {
        String email = "notFound";
        when(userService.readByEmail(email)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userController.getByEmail(email, authentication));

        verify(userService, times(1)).readByEmail(email);
        verify(mapper, times(0)).getUserResponseFromUser(new User());
    }

    @Test
    public void test_CreateAdmin() {
        User user = new User();
        user.setId(2L);
        user.setRoles(Set.of(new Role("ADMIN")));
        user.setPassword("1234");

        when(mapper.getUserFromUserCreate(UserCreateRequest.builder().build())).thenReturn(user);
        when(roleService.readByName("ADMIN")).thenReturn(new Role("ADMIN"));
        when(userService.create(eq(user), eq(Set.of(new Role("ADMIN"))))).thenReturn(user);
        userController.createAdmin(UserCreateRequest.builder().build(), authentication);

        verify(userService, times(1)).create(eq(user), eq(Set.of(new Role("ADMIN"))));
    }

    @Test
    public void test_Update() {
        User user = new User();
        user.setId(3L);
        user.setRoles(Set.of(new Role("ADMIN")));
        user.setPassword("2345");
        UserUpdateRequestWithPassword userUpdateRequestWithPassword = UserUpdateRequestWithPassword.builder().build();
        userUpdateRequestWithPassword.setOldPassword("1234");

        when(mapper.getUserFromUserUpdateRequestPass(userUpdateRequestWithPassword)).thenReturn(user);
        when(userService.update(user.getId(), user, "1234")).thenReturn(user);
        userController.update(user.getId(), authentication, userUpdateRequestWithPassword);

        verify(userService, times(1)).update(user.getId(), user, "1234");
    }

    @Test
    public void test_UpdateNames() {
        User user = new User();
        user.setId(3L);
        user.setRoles(Set.of(new Role("ADMIN")));
        user.setPassword("2345");
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder().build();
        userUpdateRequest.setLastName("la");
        userUpdateRequest.setFirstName("first");

        when(mapper.getUserFromUserUpdateRequest(userUpdateRequest)).thenReturn(user);
        when(userService.updateNames(user.getId(), user)).thenReturn(user);
        userController.updateNames(user.getId(), authentication, userUpdateRequest);

        verify(userService, times(1)).updateNames(user.getId(), user);
    }

    @Test
    public void test_Delete() {
        long id = 4L;
        when(userService.readById(id)).thenReturn(new User());
        userController.delete(id, authentication);

        verify(userService, times(1)).delete(id);
    }
}
