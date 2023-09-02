package com.board.job.service;

import com.board.job.model.entity.Image;
import com.board.job.repository.ImageRepository;
import com.board.job.service.candidate.CandidateContactService;
import com.board.job.service.employer.EmployerProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final CandidateContactService candidateContactService;
    private final EmployerProfileService employerProfileService;

    public Image createWithCandidate(long contactId, String filename) {
        var image = new Image();
        image.setContact(candidateContactService.readById(contactId));

        if (filename != null) {
            image.setProfilePicture(setImageContent(filename));
        }

        return imageRepository.save(image);
    }

    public Image createWithEmployer(long profileId, String filename) {
        var image = new Image();
        image.setProfile(employerProfileService.readById(profileId));

        if (filename != null) {
            image.setProfilePicture(setImageContent(filename));
        }

        return imageRepository.save(image);
    }

    public Image readById(long id) {
        return imageRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Image not found"));
    }

    public Image update(long id, String filename) {
        var image = readById(id);
        image.setProfilePicture(setImageContent(filename));

        return imageRepository.save(image);
    }

    public Image updateContact(long id, long contactId) {
        var image = readById(id);
        image.setContact(candidateContactService.readById(contactId));

        return imageRepository.save(image);
    }

    public Image updateProfile(long id, long profileId) {
        var image = readById(id);
        image.setProfile(employerProfileService.readById(profileId));

        return imageRepository.save(image);
    }

    public void delete(long id) {
        imageRepository.delete(readById(id));
    }

    private byte[] setImageContent(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException io) {
            throw new IllegalArgumentException(String.format("File with name %s not found", filename));
        }
    }
}
