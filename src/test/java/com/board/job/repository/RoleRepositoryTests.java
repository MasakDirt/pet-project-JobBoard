package com.board.job.repository;

import com.board.job.model.entity.Role;
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
public class RoleRepositoryTests {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleRepositoryTests(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Test
    public void testInjectedComponent() {
        AssertionsForClassTypes.assertThat(roleRepository).isNotNull();
    }

    @Test
    public void testValidFindByName() {
        String name = "ADMIN";

        Optional<Role> expected = roleRepository.findAll()
                .stream()
                .filter(role -> role.getName().equals(name))
                .findFirst();

        Optional<Role> actual = roleRepository.findByName(name);

        assertFalse(actual.isEmpty(),
                "We must get false because we have role with name ADMIN.");
        assertEquals(name, actual.get().getName(),
                "Names must be the same");
        assertEquals(expected.orElse(new Role()), actual.orElse(new Role()),
                "Roles that read by same name, must be sames");
    }

    @Test
    public void testInvalidFindByName() {
        assertTrue(roleRepository.findByName("").isEmpty(),
                "Optional must be empty because we have no role with empty name.");
    }

    @Test
    public void testValidFindAllByUsersId() {
        long userId = 4L;

        List<Role> expected = roleRepository.findAll()
                .stream()
                .filter(role -> role.getUsers()
                        .stream()
                        .anyMatch(user -> user.getId() == userId))
                .toList();

        List<Role> actual = roleRepository.findAllByUsersId(userId);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidFindAllByUsersId() {
        assertTrue(roleRepository.findAllByUsersId(0).isEmpty(),
                "List must be empty because we have no user with 0 id.");
    }
}
