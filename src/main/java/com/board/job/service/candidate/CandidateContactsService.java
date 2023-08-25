package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateContacts;
import com.board.job.repository.candidate.CandidateContactsRepository;
import com.board.job.service.PDFService;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class CandidateContactsService {
    private final UserService userService;
    private final PDFService pdfService;
    private final CandidateContactsRepository candidateContactsRepository;

    public CandidateContacts create(String pdfFilename, String photoFilename, long ownerId, CandidateContacts candidateContacts) {
        if (pdfFilename != null) {
            candidateContacts.setPdf(pdfService.create(pdfFilename));
        }

        if (photoFilename != null) {
            candidateContacts.setProfilePicture(setImageContent(photoFilename));
        }

        candidateContacts.setOwnerWithFields(userService.readById(ownerId));
        return candidateContactsRepository.save(candidateContacts);
    }

    public CandidateContacts readById(long id) {
        return candidateContactsRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Candidate not found"));
    }

    public CandidateContacts update(CandidateContacts updated) {
        readById(updated.getId());

        return updated;
    }

    public void delete(long id) {
        candidateContactsRepository.delete(readById(id));
    }

    public byte[] getWithPropertyPictureAttachedById(long id) {
        byte[] profilePicture = candidateContactsRepository.findWithPropertyPictureAttachedById(id);
        return profilePicture == null ? new byte[]{} : profilePicture;
    }

    public void saveProfilePicture(long id, byte[] imageBytes) {
        var candidateContacts = readById(id);
        candidateContacts.setProfilePicture(imageBytes);

        candidateContactsRepository.save(candidateContacts);
    }

    private byte[] setImageContent(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException io) {
            throw new IllegalArgumentException(String.format("File with name %s not found", filename));
        }
    }
}
