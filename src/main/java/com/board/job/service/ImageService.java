package com.board.job.service;

import com.board.job.exception.InvalidFile;
import com.board.job.model.entity.Image;
import com.board.job.repository.ImageRepository;
import com.board.job.service.candidate.CandidateContactService;
import com.board.job.service.employer.EmployerProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final CandidateContactService candidateContactService;
    private final EmployerProfileService employerProfileService;

    public Image createWithCandidate(long contactId, byte[] pictureBytes) {
        var image = new Image();
        image.setContact(candidateContactService.readById(contactId));
        image.setProfilePicture(pictureBytes);

        return imageRepository.save(image);
    }

    public Image createWithEmployer(long profileId, byte[] pictureBytes) {
        var image = new Image();
        image.setProfile(employerProfileService.readById(profileId));
        image.setProfilePicture(pictureBytes);

        return imageRepository.save(image);
    }

    public Image readById(long id) {
        return imageRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Image not found"));
    }

    public File getByIdCandidateImage(long id) {
        var image = readById(id);
        String fileName = "CandidateImage.jpg";
        File file = new File(fileName);

        if (Objects.nonNull(image.getContact())) {
            byte[] imageBytes = image.getProfilePicture();
            if (Objects.nonNull(imageBytes)) {
                try {
                    FileUtils.writeByteArrayToFile(file, imageBytes);

                    return file;
                } catch (IOException exception) {
                    throw new InvalidFile("Select valid file please!");
                }
            } else {
                throw new InvalidFile("This candidate have no image.");
            }
        }
        throw new IllegalArgumentException("User has no candidate contacts.");
    }

    public File getByIdEmployerImage(long id) {
        var image = readById(id);
        String fileName = "EmployerImage.jpg";
        File file = new File(fileName);

        if (Objects.nonNull(image.getProfile())) {
            byte[] imageBytes = image.getProfilePicture();
            if (Objects.nonNull(imageBytes)) {
                try {
                    FileUtils.writeByteArrayToFile(file, imageBytes);

                    return file;
                } catch (IOException exception) {
                    throw new InvalidFile("Select valid file please!");
                }
            } else {
                throw new InvalidFile("This employer have no image.");
            }
        }
        throw new IllegalArgumentException("User has no employer profile.");
    }

    public Image update(long id, byte[] pictureBytes) {
        var image = readById(id);
        image.setProfilePicture(pictureBytes);

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
