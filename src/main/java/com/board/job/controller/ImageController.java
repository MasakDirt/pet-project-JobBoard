package com.board.job.controller;

import com.board.job.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;
import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}")
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/candidate-contacts/{candidate-id}/images/{id}")
    @CrossOrigin
    @ResponseBody
    @PreAuthorize("@authCandidateContactService.isUsersSameByIdAndUserOwnerCandidateContacts" +
            "(#ownerId, #candidateId, authentication.principal)")
    public ResponseEntity<Resource> getByIdCandidateContactsImage(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long id, Authentication authentication) throws IOException {

        var file = imageService.getByIdCandidateImage(id);

        var resource = new InputStreamResource(new FileInputStream(file));
        log.info("=== GET-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(file.length())
                .body(resource);
    }

    @GetMapping("/employer-profiles/{employer-id}/images/{id}")
    @CrossOrigin
    @ResponseBody
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #employerId, authentication.principal)")
    public ResponseEntity<Resource> getByIdEmployerProfileImage(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable long id, Authentication authentication) throws IOException {

        var file = imageService.getByIdEmployerImage(id);

        var resource = new InputStreamResource(new FileInputStream(file));
        log.info("=== GET-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(file.length())
                .body(resource);
    }

    @PostMapping("/candidate-contacts/{candidate-id}/images")
    @PreAuthorize("@authCandidateContactService.isUsersSameByIdAndUserOwnerCandidateContacts" +
            "(#ownerId, #candidateId, authentication.principal)")
    public ResponseEntity<String> addCandidatePhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @RequestParam MultipartFile file, Authentication authentication) throws IOException {

        imageService.createWithCandidate(candidateId, file.getBytes());
        log.info("=== POST-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return status(HttpStatus.CREATED)
                .body("Your new profile picture successfully uploaded");
    }

    @PostMapping("/employer-profiles/{employer-id}/images")
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #employerId, authentication.principal)")
    public ResponseEntity<String> addEmployerPhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @RequestParam MultipartFile file, Authentication authentication) throws IOException {

        imageService.createWithEmployer(employerId, file.getBytes());
        log.info("=== POST-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return status(HttpStatus.CREATED)
                .body("Your new profile picture successfully uploaded");
    }

    @PutMapping("/candidate-contacts/{candidate-id}/images/{id}")
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage" +
            "(#ownerId, #candidateId, #id, authentication.principal)")
    public ResponseEntity<String> updateCandidatePhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId, @PathVariable long id,
            @RequestParam MultipartFile file, Authentication authentication) throws IOException {

        imageService.update(id, file.getBytes());
        log.info("=== PUT-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok("Your profile picture successfully updated");
    }

    @PutMapping("/employer-profiles/{employer-id}/images/{id}")
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage" +
            "(#ownerId, #employerId, #id, authentication.principal)")
    public ResponseEntity<String> updateEmployerPhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId, @PathVariable long id,
            @RequestParam MultipartFile file, Authentication authentication) throws IOException {

        imageService.update(id, file.getBytes());
        log.info("=== PUT-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok("Your profile picture successfully updated");
    }

    @DeleteMapping("/candidate-contacts/{candidate-id}/images/{id}")
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage" +
            "(#ownerId, #candidateId, #id, authentication.principal)")
    public ResponseEntity<String> deleteCandidatePhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long id, Authentication authentication) {

        imageService.delete(id);
        log.info("=== DELETE-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok("Your profile picture successfully deleted");
    }

    @DeleteMapping("/employer-profiles/{employer-id}/images/{id}")
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage" +
            "(#ownerId, #employerId, #id, authentication.principal)")
    public ResponseEntity<String> deleteEmployerPhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable long id, Authentication authentication) {

        imageService.delete(id);
        log.info("=== DELETE-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok("Your profile picture successfully deleted");
    }
}
