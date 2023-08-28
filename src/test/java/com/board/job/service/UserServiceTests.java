package com.board.job.service;

import com.board.job.model.entity.Role;
import com.board.job.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserServiceTests {
    private final UserService userService;
    private final RoleService roleService;

    private List<User> users;

    @Autowired
    public UserServiceTests(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @BeforeEach
    public void setUsers() {
        users = userService.getAll();
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(userService).isNotNull();
        AssertionsForClassTypes.assertThat(users).isNotNull();
    }

    @Test
    public void test_Valid_Create() {
        User expected = new User();
        Role role = roleService.readById(3L);

        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setEmail("user@mail.co");
        expected.setPassword("pass");
        expected.setRoles(List.of(role));

        User actual = userService.create(expected, List.of(role));
        expected.setId(actual.getId());

        assertTrue(users.size() < userService.getAll().size(),
                "After creating user list that reads before must be smaller");

        assertEquals(expected, actual,
                "After creating users must be same because they have same field values");
    }

    @Test
    public void test_Invalid_Create() {
        User user = userService.readById(4L);
        Role role = roleService.readById(2L);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> userService.create(null, List.of(role)),
                        "Null pointer exception will be thrown because we pass null user value to method."),

                () -> assertThrows(IllegalArgumentException.class, () -> userService.create(new User(), List.of(role)),
                        "Illegal argument exception will be thrown because we pass invalid user value to method."),

                () -> assertThrows(UnsupportedOperationException.class, () -> userService.create(user, List.of()),
                        "Unsupported operation exception will be thrown because we pass empty list of roles to method.")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        User expected = new User();
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setEmail("user@mail.co");
        expected.setPassword("pass");

        expected = userService.create(expected, List.of(roleService.readById(1L)));

        User actual = userService.readById(expected.getId());

        assertEquals(expected, actual,
                "Users have same id`s, so they must be the same.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> userService.readById(0),
                "We have no user with id 0, so entity not found exception will be thrown.0");
    }

    @Test
    public void test_Valid_ReadByEmail() {
        String email = "readed@mail.co";

        User expected = new User();
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setEmail(email);
        expected.setPassword("pass");

        expected = userService.create(expected, List.of(roleService.readById(4L)));

        User actual = userService.readByEmail(email);

        assertEquals(expected, actual,
                "Users have same email`s, so they must be the same.");
    }

    @Test
    public void test_Invalid_ReadByName() {
        assertThrows(EntityNotFoundException.class, () -> userService.readByEmail(""),
                "Entity not found exception will be thrown because we have no user with empty email.");
    }

    @Test
    public void test_Valid_Update_User() {
        String firstName = "New";
        User unexpected = userService.readById(1L);
        String oldFirstName = unexpected.getFirstName();
        String oldLastName = unexpected.getLastName();
        String password = unexpected.getPassword();

        unexpected.setFirstName(firstName);

        User actual = userService.update(unexpected);

        assertAll(
                () -> assertEquals(oldLastName, actual.getLastName(),
                        "We are not updated last name so they must be same"),
                () -> assertEquals(password, actual.getPassword(),
                        "We are not updated password so they must be same"),

                () -> assertNotEquals(oldFirstName, actual.getFirstName(),
                        "After updating user, his first name must check in.")
        );
    }

    @Test
    public void test_Invalid_Update_User() {
        assertThrows(NullPointerException.class, () -> userService.update(null),
                "Null pointer exception will be thrown because we pass null user value to method.");

        assertThrows(EntityNotFoundException.class, () -> userService.update(new User()),
                "Entity not found exception will be thrown because we pass invalid user value to method.");
    }

    @Test
    public void test_Valid_Update() {
        String lastName = "New";
        User unexpected = userService.readById(2L);
        String oldFirstName = unexpected.getFirstName();
        String oldLastName = unexpected.getLastName();
        String email = unexpected.getEmail();

        unexpected.setLastName(lastName);

        User actual = userService.update(unexpected, "2222");

        assertAll(
                () -> assertEquals(oldFirstName, actual.getFirstName(),
                        "We are not updated last name so they must be same"),
                () -> assertEquals(email, actual.getEmail(),
                        "We are not updated email so they must be same"),

                () -> assertNotEquals(oldLastName, actual.getFirstName(),
                        "After updating user, his first name must check in.")
        );
    }

    @Test
    public void test_Invalid_Update() {
        User user = userService.readById(5L);
        assertThrows(NullPointerException.class, () -> userService.update(null, ""),
                "Null pointer exception will be thrown because we pass null user value to method.");

        assertThrows(EntityNotFoundException.class, () -> userService.update(new User(), ""),
                "Entity not found exception will be thrown because we pass invalid user value to method.");

        assertThrows(ResponseStatusException.class, () -> userService.update(user, "1234"),
                "Entity not found exception will be thrown because we pass invalid user value to method.");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 4L;

        userService.delete(id);

        assertTrue(users.size() > userService.getAll().size(),
                "Users size that read before deleting must be bigger");

        assertThrows(EntityNotFoundException.class, () -> userService.readById(id),
                "Entity not found exception will be thrown because we delete this user.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> userService.delete(0),
                "Entity not found exception will be thrown because we have no this user.");
    }

    @Test
    public void test_Valid_UpdateUserRolesAndGetUser() {
        long userId = 1L;
        String roleName = "USER";

        User unexpected = userService.readById(userId);
        List<Role> roles = unexpected.getRoles();

        User actual = userService.updateUserRolesAndGetUser(userId, roleName);

        assertNotEquals(roles, actual.getRoles());
    }
}
