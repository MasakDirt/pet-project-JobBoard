package com.board.job.service;

import com.board.job.model.entity.Role;
import com.board.job.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role create(String name) {
        nameChecking(name);
        return roleRepository.save(new Role(name));
    }

    private void nameChecking(String name) throws IllegalArgumentException {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name must contain letters!");
        }
    }

    public Role readById(long id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Role with id not found"));
    }

    public Role readByName(String name) {
        return roleRepository.findByName(name.toUpperCase()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Role with name %s not found", name)));
    }

    public Role update(long id, String newName) {
        nameChecking(newName);
        Role old = readById(id);
        old.setName(newName);

        return roleRepository.save(old);
    }

    public void delete(long id) {
        roleRepository.delete(readById(id));
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public List<Role> getAllByUserId(long id) {
        return roleRepository.findAllByUsersId(id);
    }

    public List<Role> getAllByEmail(String email) {
        return roleRepository.findAllByUsersEmail(email);
    }
}
