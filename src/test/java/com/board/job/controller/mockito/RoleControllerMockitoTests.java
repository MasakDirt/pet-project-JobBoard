package com.board.job.controller.mockito;

import com.board.job.controller.RoleController;
import com.board.job.model.dto.role.RoleResponse;
import com.board.job.model.entity.Role;
import com.board.job.model.mapper.RoleMapper;
import com.board.job.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RoleControllerMockitoTests {
    @InjectMocks
    private RoleController roleController;
    @Mock
    private RoleService roleService;
    @Mock
    private RoleMapper mapper;
    @Mock
    Authentication authentication;

    @Test
    public void test_GetAll() {
        List<Role> roles = List.of(new Role("ROLE"), new Role("NEW"));

        when(roleService.getAll()).thenReturn(roles);
        when(mapper.getRoleResponseFromRole(new Role("ROLE"))).thenReturn(RoleResponse.builder().build());

        roleController.getAll(authentication);

        verify(roleService, times(1)).getAll();
    }

    @Test
    public void test_GetAllUserRoles() {
        long userID = 2L;
        List<Role> roles = List.of(new Role("USERS"));

        when(roleService.getAllByUserId(userID)).thenReturn(roles);
        when(mapper.getRoleResponseFromRole(new Role("USERS"))).thenReturn(RoleResponse.builder().build());

        roleController.getAllUserRoles(userID, authentication);

        verify(roleService, times(1)).getAllByUserId(userID);
    }

    @Test
    public void test_Empty_UserRoles() {
        long userID = 10L;

        when(roleService.getAllByUserId(userID)).thenReturn(List.of());
        roleController.getAllUserRoles(userID, authentication);

        verify(roleService, times(1)).getAllByUserId(userID);
        verify(mapper, times(0)).getRoleResponseFromRole(new Role());
    }

    @Test
    public void test_GetRole() {
        long id = 5L;
        Role role = new Role("EX");
        role.setId(id);
        RoleResponse expected = RoleResponse.builder().build();
        expected.setId(id);
        expected.setName(role.getName());

        when(roleService.readById(id)).thenReturn(role);
        when(mapper.getRoleResponseFromRole(role)).thenReturn(expected);
        RoleResponse actual = roleController.getRole(id, authentication);

        verify(roleService, times(1)).readById(id);
        verify(mapper, times(1)).getRoleResponseFromRole(role);

        assertEquals(expected, actual);
    }

    @Test
    public void test_NotFound_GetRole() {
        long id = 10L;

        when(roleService.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> roleController.getRole(id, authentication));

        verify(roleService, times(1)).readById(id);
        verify(mapper, times(0)).getRoleResponseFromRole(new Role());
    }

    @Test
    public void test_getRoleByName() {
        String name = "NEW";
        Role role = new Role(name);
        RoleResponse expected = RoleResponse.builder().build();
        expected.setName(role.getName());

        when(roleService.readByName(name)).thenReturn(role);
        when(mapper.getRoleResponseFromRole(role)).thenReturn(expected);
        RoleResponse actual = roleController.getRoleByName(name, authentication);

        verify(roleService, times(1)).readByName(name);
        verify(mapper, times(1)).getRoleResponseFromRole(role);

        assertEquals(expected, actual);
    }

    @Test
    public void test_NotFound_getRoleByName() {
        String name = "INVALID";

        when(roleService.readByName(name)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> roleController.getRoleByName(name, authentication));

        verify(roleService, times(1)).readByName(name);
        verify(mapper, times(0)).getRoleResponseFromRole(new Role());
    }

    @Test
    public void test_Create() {
        String name = "CREATED";

        when(roleService.create(name)).thenReturn(new Role(name));
        roleController.create(name, authentication);

        verify(roleService, times(1)).create(name);
    }

    @Test
    public void test_Update() {
        long id = 2L;
        String name = "UPDATED";

        when(roleService.readById(id)).thenReturn(new Role(name));
        when(roleService.update(id, name)).thenReturn(new Role(name));
        roleController.update(id, name, authentication);

        verify(roleService, times(1)).readById(id);
        verify(roleService, times(1)).update(id, name);
    }

    @Test
    public void test_Delete() {
        long id = 2L;

        when(roleService.readById(id)).thenReturn(new Role("DELETED"));
        roleController.delete(id, authentication);

        verify(roleService, times(1)).readById(id);
        verify(roleService, times(1)).delete(id);
    }
}
