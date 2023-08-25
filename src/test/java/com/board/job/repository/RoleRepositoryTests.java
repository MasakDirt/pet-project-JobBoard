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
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(roleRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindByName() {
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
    public void test_Invalid_FindByName() {
        assertTrue(roleRepository.findByName("").isEmpty(),
                "Optional must be empty because we have no role with empty name.");
    }
}
