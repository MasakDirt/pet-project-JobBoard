package com.board.job.service;

import com.board.job.model.entity.Role;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class RoleServiceTests {
    private final RoleService roleService;

    List<Role> roles;

    @Autowired
    public RoleServiceTests(RoleService roleService) {
        this.roleService = roleService;
    }

    @BeforeEach
    public void setRoles() {
        roles = roleService.getAll();
    }

    @Test
    public void testInjectedComponents() {
        AssertionsForClassTypes.assertThat(roleService).isNotNull();
        AssertionsForClassTypes.assertThat(roles).isNotNull();
    }

    @Test
    public void testGetAll() {
        assertFalse(roleService.getAll().isEmpty(),
                "All roles list must be not empty.");
        assertEquals(roles, roleService.getAll(),
                "Lists of all roles must be the same");
    }

    @Test
    public void testValidCreate() {
        String name = "NEWROLE";

        Role expected = new Role(name);

        Role actual = roleService.create(name);
        expected.setId(actual.getId());

        assertTrue(roles.size() < roleService.getAll().size(),
                "After creating role, list that reads before must be smaller than that reads now.");

        assertEquals(name, actual.getName(),
                "Name of created role must be the same with expected name");

        assertEquals(expected, actual,
                "Roles are created by same name, so they must be same!");
    }

    @Test
    public void testInvalidCreate() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> roleService.create(null),
                        "Null pointer exception will be thrown because we cannot pass null name!"),

                () -> assertThrows(IllegalArgumentException.class, () -> roleService.create("   "),
                        "Illegal argument exception will be thrown because we shouldn`t pass empty name."),

                () -> assertThrows(ConstraintViolationException.class, () -> roleService.create("invalid"),
                        "Constraint violation exception will be thrown because name should be in upper case!")
        );
    }

    @Test
    public void testValidReadById() {
        Role expected = roleService.create("READ");
        Role actual = roleService.readById(expected.getId());

        assertEquals(expected, actual,
                "Roles has sames id, so they must be the same");
    }

    @Test
    public void testInvalidReadByID() {
        assertThrows(EntityNotFoundException.class, () -> roleService.readById(0),
                "Entity not found exception will be thrown because we have no role with id 0.");
    }

    @Test
    public void testValidReadByName() {
        String name = "WELL";

        Role expected = roleService.create(name);
        Role actual = roleService.readByName(name);

        assertEquals(expected, actual,
                "Roles has sames names, so they must be the same");
    }

    @Test
    public void testInvalidReadByName() {
        assertThrows(EntityNotFoundException.class, () -> roleService.readByName(""),
                "Entity not found exception will be thrown because we have no role with empty name.");
    }

    @Test
    public void testValidUpdate() {
        String oldName = "OLD";
        String newName = "NEW";

        Role unexpected = roleService.create(oldName);
        Role actual = roleService.update(unexpected.getId(), newName);

        assertAll(
                () -> assertEquals(unexpected.getId(), actual.getId(),
                        "We do not update id`s so they must be same"),

                () -> assertEquals(newName, actual.getName(),
                        "New name must be same in updated role"),

                () -> assertNotEquals(oldName, actual.getName())
        );
    }

    @Test
    public void testInvalidUpdate() {
        long id = 2L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> roleService.update(0, "ROLE"),
                        "Entity not found exception will be thrown because we have no role with id 0."),

                () -> assertThrows(NullPointerException.class, () -> roleService.update(id, null),
                        "Null pointer exception will be thrown because we cannot pass null name!"),

                () -> assertThrows(IllegalArgumentException.class, () -> roleService.update(id, "   "),
                        "Illegal argument exception will be thrown because we shouldn`t pass empty name.")
        );
    }

    @Test
    public void testValidDelete() {
        long id = 1L;

        roleService.delete(id);

        assertTrue(roles.size() > roleService.getAll().size(),
                "After deleting role, list that reads before must be bigger than which reads now.");

        assertThrows(EntityNotFoundException.class, () -> roleService.readById(id),
                "We delete role with this id, so here must throws an exception");
    }

    @Test
    public void testInvalidDelete() {
        assertThrows(EntityNotFoundException.class, () -> roleService.delete(0),
                "Entity not found exception will be thrown because we have no role with id 0.");
    }

    @Test
    public void testValidGetAllByUserId() {
        long userId = 4L;

        List<Role> expected = roleService.getAll()
                .stream()
                .filter(role -> role.getUsers()
                        .stream()
                        .anyMatch(user -> user.getId() == userId))
                .toList();

        List<Role> actual = roleService.getAllByUserId(userId);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidGetAllByUserId() {
        assertTrue(roleService.getAllByUserId(0).isEmpty(),
                "List must be empty because we have no user with 0 id.");
    }
}
