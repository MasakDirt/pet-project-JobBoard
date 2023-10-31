package com.board.job.service;

import com.board.job.model.entity.Image;
import com.board.job.repository.ImageRepository;
import com.board.job.service.candidate.CandidateContactService;
import com.board.job.service.employer.EmployerProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Image update(long id, byte[] pictureBytes) {
        var image = readById(id);
        image.setProfilePicture(pictureBytes);

        return imageRepository.save(image);
    }

    public void delete(long id) {
        imageRepository.delete(readById(id));
    }
}
