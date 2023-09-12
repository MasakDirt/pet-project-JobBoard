package com.board.job.controller;

import com.board.job.service.PDFService;
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

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/candidate-contacts/{contact-id}/pdfs")
public class PDFFileController {
    private final PDFService pdfService;

    @GetMapping("/{id}")
    @CrossOrigin
    @ResponseBody
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.principal)")
    public ResponseEntity<Resource> getById(
            @PathVariable("owner-id") long ownerId, @PathVariable long id,
            Authentication authentication) throws IOException {

        var file = pdfService.getPDFFile(id);

        var mediaType = MediaType.parseMediaType("application/pdf");
        var resource = new InputStreamResource(new FileInputStream(file));
        log.info("=== GET-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(mediaType)
                .contentLength(file.length())
                .body(resource);

    }

    @PostMapping
    @PreAuthorize("@authCandidateContactService.isUsersSameByIdAndUserOwnerCandidateContacts" +
            "(#ownerId, #candidateContactId, authentication.principal)")
    public ResponseEntity<String> create(@PathVariable("owner-id") long ownerId,
                                         @PathVariable("contact-id") long candidateContactId,
                                         @RequestParam MultipartFile file,
                                         Authentication authentication) throws IOException {

        pdfService.create(candidateContactId, file.getBytes());
        log.info("=== POST-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("PDF file successfully uploaded");
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authPDFService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF" +
            "(#ownerId, #candidateContactId, #id, authentication.principal)")
    public ResponseEntity<String> update(
            @PathVariable("owner-id") long ownerId,
            @PathVariable("contact-id") long candidateContactId, @PathVariable long id,
            @RequestParam MultipartFile file, Authentication authentication) throws Exception {

        pdfService.update(id, file.getBytes());
        log.info("=== PUT-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok("PDF file successfully updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authPDFService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF" +
            "(#ownerId, #candidateContactId, #id, authentication.principal)")
    public ResponseEntity<String> delete(@PathVariable("owner-id") long ownerId,
                                         @PathVariable("contact-id") long candidateContactId, @PathVariable long id,
                                         Authentication authentication) {
        pdfService.delete(id);
        log.info("=== PUT-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok("PDF file successfully deleted");
    }
}
