package com.board.job.repository;

import com.board.job.model.entity.User;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserRepositoryTests {
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(userRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindByEmail() {
        String email = "admin@mail.co";

        Optional<User> expected = userRepository.findAll()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        Optional<User> actual = userRepository.findByEmail(email);

        assertEquals(email, actual.get().getEmail(),
                "Emails must be the same");
        assertEquals(expected.orElse(new User()), actual.orElse(new User()),
                "Users that read by same email, must be sames");
    }

    @Test
    public void test_Valid_FindAllByRolesName() {
        String name = "USER";

        List<User> expected = userRepository.findAll()
                .stream()
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(role -> role.getName().equals(name))
                )
                .toList();

        List<User> actual = userRepository.findAllByRolesName(name);

        assertFalse(actual.isEmpty(),
                "We must get false because in this employer company users are available.");
        assertTrue(userRepository.findAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same role name!");
    }

    @Test
    public void test_Invalid_FindAllByRolesName() {
        assertTrue(userRepository.findAllByRolesName("").isEmpty(),
                "We have no user with role name '', so here must be empty list!");
    }

    @Test
    public void test_Valid_FindAllByFirstName() {
        String firstname = "Nikole";

        List<User> expected = userRepository.findAll()
                .stream()
                .filter(user -> user.getFirstName().equals(firstname))
                .toList();

        List<User> actual = userRepository.findAllByFirstName(firstname);

        assertFalse(actual.isEmpty(),
                "We must get false because in this employer company users are available.");
        assertTrue(userRepository.findAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same first name!");
    }

    @Test
    public void test_Invalid_FindAllByFirstName() {
        assertTrue(userRepository.findAllByFirstName("").isEmpty(),
                "We have no user with first name '', so here must be empty list!");
    }

    @Test
    public void test_Valid_FindAllByLastName() {
        String lastname = "Jackson";

        List<User> expected = userRepository.findAll()
                .stream()
                .filter(user -> user.getLastName().equals(lastname))
                .toList();

        List<User> actual = userRepository.findAllByLastName(lastname);

        assertFalse(actual.isEmpty(),
                "We must get false because in this employer company users are available.");
        assertTrue(userRepository.findAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same last name!");
    }

    @Test
    public void test_Invalid_FindAllByLastName() {
        assertTrue(userRepository.findAllByLastName("").isEmpty(),
                "We have no user with last name '', so here must be empty list!");
    }

    @Test
    public void test_Valid_findAllByFirstNameAndLastName() {
        String firstname = "Nikole";
        String lastname = "Jackson";

        List<User> expected = userRepository.findAll()
                .stream()
                .filter(user -> user.getFirstName().equals(firstname) &&
                        user.getLastName().equals(lastname))
                .toList();

        List<User> actual = userRepository.findAllByFirstNameAndLastName(firstname, lastname);

        assertFalse(actual.isEmpty(),
                "We must get false because in this employer company users are available.");
        assertTrue(userRepository.findAll().size() > actual.size(),
                "Actual users size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same first and last names!");
    }

    @Test
    public void test_Invalid_findAllByFirstNameAndLastName() {
        assertTrue(userRepository.findAllByFirstNameAndLastName("", "").isEmpty(),
                "We have no user with first and last blank names, so here must be empty list!");

        assertTrue(userRepository.findAllByFirstNameAndLastName("", "Jackson").isEmpty(),
                "We have no user with first blank name, so here must be empty list!");

        assertTrue(userRepository.findAllByFirstNameAndLastName("Jackson", "").isEmpty(),
                "We have no user with last blank name, so here must be empty list!");
    }
}