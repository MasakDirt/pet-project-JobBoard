package com.board.job.service.mockito;

import com.board.job.model.entity.Role;
import com.board.job.repository.RoleRepository;
import com.board.job.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RoleServiceMockitoTests {
    @InjectMocks
    private RoleService roleService;
    @Mock
    private RoleRepository roleRepository;

    @Test
    public void test_Create() {
        roleService.create("NAME");

        verify(roleRepository, times(1)).save(new Role("NAME"));
    }

    @Test
    public void test_ReadById() {
        long id = 3L;

        when(roleRepository.findById(id)).thenReturn(Optional.of(new Role("TEST")));
        Role actual = roleService.readById(id);

        assertEquals(new Role("TEST"), actual);
    }

    @Test
    public void test_ReadByName() {
        String name = "READ";

        when(roleRepository.findByName(name)).thenReturn(Optional.of(new Role(name)));
        Role actual = roleService.readByName(name);

        assertEquals(new Role(name), actual);
    }

    @Test
    public void test_Update() {
        String name = "UPDATE";

        when(roleRepository.findById(3L)).thenReturn(Optional.of(new Role("OLD")));
        roleService.update(3L, name);

        verify(roleRepository, times(1)).save(new Role(name));
    }

    @Test
    public void test_Delete() {
        when(roleRepository.findById(3L)).thenReturn(Optional.of(new Role("OLD")));
        roleService.delete(3L);

        verify(roleRepository, times(1)).delete(new Role("OLD"));
    }

    @Test
    public void test_GetAll() {
        roleService.getAll();

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    public void test_GetAllByUserId() {
        long id = 3L;
        roleService.getAllByUserId(id);

        verify(roleRepository, times(1)).findAllByUsersId(id);
    }
}
