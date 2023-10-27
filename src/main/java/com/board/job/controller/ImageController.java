package com.board.job.controller;

import com.board.job.service.ImageService;
import com.google.common.io.Files;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
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

import java.io.File;
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
    @GetMapping("/images/no-image")
    public Resource getNoImage() throws IOException{
        return new ByteArrayResource(Files.toByteArray(new File("files/photos/noUserPhoto.jpg")));
    }

    @GetMapping("/images/no-image/header")
    public Resource getNoImageForHeader() throws IOException{
        return new ByteArrayResource(Files.toByteArray(new File("files/photos/noUserPhoto.jpg")));
    }

    @GetMapping(value = "/candidate-contacts/{candidate-id}/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @PreAuthorize("@authCandidateContactService.isUsersSameByIdAndUserOwnerCandidateContacts" +
            "(#ownerId, #candidateId, authentication.name)")
    public Resource getByIdCandidateContactsImage(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long id, Authentication authentication) throws IOException {
        var file = imageService.readById(id);
        log.info("=== GET-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ByteArrayResource(file.getProfilePicture());
    }

    @GetMapping(value = "/candidate-contacts/{candidate-id}/images/{id}/header", produces = MediaType.IMAGE_JPEG_VALUE)
    @PreAuthorize("@authCandidateContactService.isUsersSameByIdAndUserOwnerCandidateContacts" +
            "(#ownerId, #candidateId, authentication.name)")
    public Resource getByIdCandidateContactsImageForHeader(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long id, Authentication authentication) throws IOException {
        var file = imageService.readById(id);
        log.info("=== GET-CANDIDATE-IMAGE-HEADER === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ByteArrayResource(file.getProfilePicture());
    }

    @GetMapping(value = "/employer-profiles/{employer-id}/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE', 'EMPLOYER')")
    public Resource getByIdEmployerProfileImage(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable long id, Authentication authentication) throws IOException {

        var file = imageService.readById(id);
        log.info("=== GET-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return new ByteArrayResource(file.getProfilePicture());
    }

    @PostMapping("/candidate-contacts/{candidate-id}/images")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authCandidateContactService.isUsersSameByIdAndUserOwnerCandidateContacts" +
            "(#ownerId, #candidateId, authentication.name)")
    public void addCandidatePhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @RequestParam MultipartFile file, Authentication authentication, HttpServletResponse response) throws IOException {

        imageService.createWithCandidate(candidateId, file.getBytes());
        log.info("=== POST-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        response.sendRedirect(String.format("/api/users/%d/candidate-contacts/%d", ownerId, candidateId));
    }

    @PostMapping("/employer-profiles/{employer-id}/images")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #employerId, authentication.name)")
    public ResponseEntity<String> addEmployerPhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @RequestParam MultipartFile file, Authentication authentication) throws IOException {

        imageService.createWithEmployer(employerId, file.getBytes());
        log.info("=== POST-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return status(HttpStatus.CREATED)
                .body("Your new profile picture successfully uploaded");
    }

    @PostMapping("/candidate-contacts/{candidate-id}/images/{id}/update")
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage" +
            "(#ownerId, #candidateId, #id, authentication.name)")
    public void updateCandidatePhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId, @PathVariable long id,
            @RequestBody MultipartFile file, Authentication authentication, HttpServletResponse response) throws IOException {

        var image = imageService.update(id, file.getBytes());
        log.info("=== PUT-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        response.sendRedirect(String.format("/api/users/%d/candidate-contacts/%d", ownerId, candidateId));
    }

    @PutMapping("/employer-profiles/{employer-id}/images/{id}")
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public ResponseEntity<String> updateEmployerPhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId, @PathVariable long id,
            @RequestBody MultipartFile file, Authentication authentication) throws IOException {

        imageService.update(id, file.getBytes());
        log.info("=== PUT-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok("Your profile picture successfully updated");
    }

    @GetMapping("/candidate-contacts/{candidate-id}/images/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage" +
            "(#ownerId, #candidateId, #id, authentication.name)")
    public void deleteCandidatePhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long id, Authentication authentication, HttpServletResponse response) throws IOException {

        imageService.delete(id);
        log.info("=== DELETE-CANDIDATE-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());
        response.sendRedirect(String.format("/api/users/%d/candidate-contacts/%d", ownerId, candidateId));
    }

    @DeleteMapping("/employer-profiles/{employer-id}/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authImageService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public ResponseEntity<String> deleteEmployerPhoto(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable long id, Authentication authentication) {

        imageService.delete(id);
        log.info("=== DELETE-EMPLOYER-IMAGE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok("Your profile picture successfully deleted");
    }
}
