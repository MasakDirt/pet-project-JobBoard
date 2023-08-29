package com.board.job.service;

import com.board.job.exception.UserIsNotEmployer;
import com.board.job.model.entity.Role;
import com.board.job.model.entity.User;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.entity.employer.EmployerProfile;
import jakarta.persistence.EntityNotFoundException;
import org.apache.cassandra.utils.Pair;
import org.assertj.core.api.AssertionsForClassTypes;
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
    public void test_GetAll() {
        assertFalse(userService.getAll().isEmpty(),
                "All users list must be not empty.");
        assertEquals(users, userService.getAll(),
                "Lists of all users must be the same");
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
                "We have no user with id 0, so entity not found exception will be thrown.");
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
        String roleName = "USER";

        User unexpected = new User();

        unexpected.setFirstName("First");
        unexpected.setLastName("Last");
        unexpected.setEmail("email@mail.co");
        unexpected.setPassword("pass");
        unexpected.setRoles(List.of(roleService.readById(1), roleService.readById(3), roleService.readById(4)));
        List<Role> roles = unexpected.getRoles();

        unexpected = userService.create(unexpected, roles);
        User actual = userService.updateUserRolesAndGetUser(unexpected.getId(), roleName);

        assertNotEquals(roles, actual.getRoles(),
        "We must get list of roles with new role 'USER'");
    }

    @Test
    public void test_Invalid_UpdateUserRolesAndGetUser() {
        User user = userService.readById(2L);
        String role = "USER";

        assertEquals(user, userService.updateUserRolesAndGetUser(user.getId(), role),
                "We already have this role in this user, so here must return same user");

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRolesAndGetUser(user.getId(), ""),
                "Entity not found exception will be thrown because we have no this role!");

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRolesAndGetUser(0, role),
                "Entity not found exception will be thrown because we have no this user!");
    }

    @Test
    public void test_Valid_GetEmployerData() {
        long id = 1L;
        User user = userService.readById(id);
        Pair<EmployerCompany, EmployerProfile> expected = Pair.create(user.getEmployerCompany(), user.getEmployerProfile());

        Pair<EmployerCompany, EmployerProfile> actual = userService.getEmployerData(id);

        assertEquals(expected, actual,
                "Pairs reads by one user id`s so they must be sames");
    }

    @Test
    public void test_Invalid_GetEmployerData() {
        assertThrows(UserIsNotEmployer.class, () -> userService.getEmployerData(5L),
                "User have no employer profile or company so here must be exception.");
        assertThrows(EntityNotFoundException.class, () -> userService.getEmployerData(0),
                "We have no user with id 0, so entity not found exception will be thrown.");
    }

    @Test
    public void test_Valid_GetAllByRoleName() {
        String name = "USER";

        List<User> expected = userService.getAll()
                .stream()
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(role -> role.getName().equals(name))
                )
                .toList();

        List<User> actual = userService.getAllByRoleName(name);

        assertFalse(actual.isEmpty(),
                "We must get false because in this list users are available.");
        assertTrue(userService.getAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same role name!");
    }

    @Test
    public void test_Invalid_GetAllByRolesName() {
        assertTrue(userService.getAllByRoleName("").isEmpty(),
                "We have no user with role name '', so here must be empty list!");
    }

    @Test
    public void test_Valid_GetAllByFirstName() {
        String firstname = "Nikole";

        List<User> expected = userService.getAll()
                .stream()
                .filter(user -> user.getFirstName().equals(firstname))
                .toList();

        List<User> actual = userService.getAllByFirstName(firstname);

        assertFalse(actual.isEmpty(),
                "We must get false because in this list users are available.");
        assertTrue(userService.getAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same first name!");
    }

    @Test
    public void test_Invalid_GetAllByFirstName() {
        assertTrue(userService.getAllByFirstName("").isEmpty(),
                "We have no user with first name '', so here must be empty list!");
    }

    @Test
    public void test_Valid_GetAllByLastName() {
        String lastname = "Jackson";

        List<User> expected = userService.getAll()
                .stream()
                .filter(user -> user.getLastName().equals(lastname))
                .toList();

        List<User> actual = userService.getAllByLastName(lastname);

        assertFalse(actual.isEmpty(),
                "We must get false because in this list users are available.");
        assertTrue(userService.getAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same last name!");
    }

    @Test
    public void test_Invalid_GetAllByLastName() {
        assertTrue(userService.getAllByLastName("").isEmpty(),
                "We have no user with last name '', so here must be empty list!");
    }

    @Test
    public void test_Valid_GetAllByFirstNameAndLastName() {
        String firstname = "Nikole";
        String lastname = "Jackson";

        List<User> expected = userService.getAll()
                .stream()
                .filter(user -> user.getFirstName().equals(firstname) &&
                        user.getLastName().equals(lastname))
                .toList();

        List<User> actual = userService.getAllByFirstNameAndLastName(firstname, lastname);

        assertFalse(actual.isEmpty(),
                "We must get false because in this list users are available.");
        assertTrue(userService.getAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same first and last names!");
    }

    @Test
    public void test_Invalid_GetAllByFirstNameAndLastName() {
        assertTrue(userService.getAllByFirstNameAndLastName("", "").isEmpty(),
                "We have no user with first and last blank names, so here must be empty list!");

        assertTrue(userService.getAllByFirstNameAndLastName("", "Jackson").isEmpty(),
                "We have no user with first blank name, so here must be empty list!");

        assertTrue(userService.getAllByFirstNameAndLastName("Jackson", "").isEmpty(),
                "We have no user with last blank name, so here must be empty list!");
    }
}
