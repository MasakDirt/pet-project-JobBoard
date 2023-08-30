package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.repository.candidate.CandidateContactRepository;
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
    private final CandidateContactRepository candidateContactRepository;

    public CandidateContact create(String pdfFilename, String photoFilename, long ownerId, CandidateContact candidateContact) {
        if (pdfFilename != null) {
            candidateContact.setPdf(pdfService.create(pdfFilename));
        }

        if (photoFilename != null) {
            candidateContact.setProfilePicture(setImageContent(photoFilename));
        }

        candidateContact.setOwnerWithFields(userService.readById(ownerId));
        return candidateContactRepository.save(candidateContact);
    }

    public CandidateContact readById(long id) {
        return candidateContactRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Candidate not found"));
    }

    public CandidateContact update(CandidateContact updated) {
        readById(updated.getId());

        return candidateContactRepository.save(updated);
    }

    public void delete(long id) {
        candidateContactRepository.delete(readById(id));
    }

    public byte[] getWithPropertyPictureAttachedById(long id) {
        byte[] profilePicture = candidateContactRepository.findWithPropertyPictureAttachedById(id);
        return profilePicture == null ? new byte[]{} : profilePicture;
    }

    public void saveProfilePicture(long id, byte[] imageBytes) {
        var candidateContacts = readById(id);
        candidateContacts.setProfilePicture(imageBytes);

        candidateContactRepository.save(candidateContacts);
    }

    private byte[] setImageContent(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException io) {
            throw new IllegalArgumentException(String.format("File with name %s not found", filename));
        }
    }
}
