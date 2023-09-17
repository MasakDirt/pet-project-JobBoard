package com.board.job.service.mockito;

import com.board.job.model.entity.Role;
import com.board.job.model.entity.User;
import com.board.job.repository.UserRepository;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class UserServiceMockitoTests {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;

    @Test
    public void test_Create() {
        Role role = new Role("TEST");
        User expected = new User();
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setEmail("mail@m.co");
        expected.setPassword("new pass");

        when(passwordEncoder.encode(expected.getPassword())).thenReturn("OKDDOEJJFOKFOKF");

        userService.create(expected, Set.of(role));

        verify(userRepository, times(1)).save(expected);
    }

    @Test
    public void test_ReadById() {
        long id = 2L;
        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));
        userService.readById(id);

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void test_ReadByEmail() {
        String email = "user@mail.co";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        userService.readByEmail(email);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void test_Valid_Update() {
        String oldPass = "1111";
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("mail@m.co");
        user.setPassword("new pass");
        user.setRoles(Set.of());

        when(passwordEncoder.matches(oldPass, user.getPassword())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new pass")).thenReturn("LPEFLFlfemkfflF:LKD");
        when(roleService.getAllByUserId(user.getId())).thenReturn(List.of());

        userService.update(user.getId(), user, oldPass);
    }

    @Test
    public void test_Invalid_Password_Update() {
        String oldPass = "1111";
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("mail@m.co");
        user.setPassword("new pass");
        user.setRoles(Set.of());

        when(passwordEncoder.matches(oldPass, user.getPassword())).thenReturn(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Assertions.assertThrows(ResponseStatusException.class, () -> userService.update(user.getId(), user, oldPass));
    }

    @Test
    public void test_Delete() {
        long id = 1L;
        User user = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.delete(id);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void test_GetAll() {
        userService.getAll();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void test_GetAllByRoleName() {
        userService.getAllByRoleName("Name");

        verify(userRepository, times(1)).findAllByRolesName("Name");
    }

    @Test
    public void test_GetAllByFirstName() {
        userService.getAllByFirstName("First");

        verify(userRepository, times(1)).findAllByFirstName("First");
    }

    @Test
    public void test_GetAllByLastName() {
        userService.getAllByLastName("Last");

        verify(userRepository, times(1)).findAllByLastName("Last");
    }

    @Test
    public void test_GetAllByFirstNameAndLastName() {
        userService.getAllByFirstNameAndLastName("First", "Last");

        verify(userRepository, times(1)).findAllByFirstNameAndLastName("First", "Last");
    }
}
