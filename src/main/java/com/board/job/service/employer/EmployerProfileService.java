package com.board.job.service.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.repository.employer.EmployerProfileRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class EmployerProfileService {
    private final UserService userService;
    private final EmployerProfileRepository employerProfileRepository;

    public EmployerProfile create(String photoFileName, long ownerId, EmployerProfile employerProfile) {
        if (photoFileName != null) {
            employerProfile.setProfilePicture(setImageContent(photoFileName));
        }

        employerProfile.setOwnerWithName(userService.readById(ownerId));
        return employerProfileRepository.save(employerProfile);
    }

    public EmployerProfile readById(long id) {
        return employerProfileRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Employer profile not found"));
    }

    public EmployerProfile update(EmployerProfile updated) {
        readById(updated.getId());

        return employerProfileRepository.save(updated);
    }

    public void delete(long id) {
        employerProfileRepository.delete(readById(id));
    }

    public EmployerProfile getWithPropertyPictureAttachedById(long id) {
        return employerProfileRepository.findWithPropertyPictureAttachedById(id).orElseThrow(() ->
                new EntityNotFoundException("Employer profile not found"));
    }

    public void saveProfilePicture(long id, byte[] imageBytes) {
        var employerProfile = readById(id);
        employerProfile.setProfilePicture(imageBytes);

        employerProfileRepository.save(employerProfile);
    }

    private byte[] setImageContent(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException io) {
            throw new IllegalArgumentException(String.format("File with name %s not found", filename));
        }
    }
}
