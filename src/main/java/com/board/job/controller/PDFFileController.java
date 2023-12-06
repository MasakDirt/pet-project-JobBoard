package com.board.job.controller;

import com.board.job.service.PDFService;
import com.board.job.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static com.board.job.controller.ControllerHelper.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/candidate-contacts/{contact-id}/pdfs")
public class PDFFileController {
    private final UserService userService;
    private final PDFService pdfService;

    @GetMapping("/{id}")
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.name)")
    public ModelAndView getById(
            @PathVariable("owner-id") long ownerId, @PathVariable long id,
            Authentication authentication, ModelMap map) {
        var pdfBase64 = pdfService.getBase64Pdf(id);
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("pdfBase64", pdfBase64);
        log.info("=== GET-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("candidates/pdf-get", map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authCandidateContactService.isUsersSameByIdAndUserOwnerCandidateContacts" +
            "(#ownerId, #candidateContactId, authentication.name)")
    public void create(@PathVariable("owner-id") long ownerId,
                       @PathVariable("contact-id") long candidateContactId,
                       @RequestParam MultipartFile file,
                       Authentication authentication, HttpServletResponse response) throws IOException {

        pdfService.create(candidateContactId, file.getBytes());
        log.info("=== POST-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, String.format("/api/users/%d/candidate-contacts/%d", ownerId, candidateContactId));
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("@authPDFService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF" +
            "(#ownerId, #candidateContactId, #id, authentication.name)")
    public void update(
            @PathVariable("owner-id") long ownerId,
            @PathVariable("contact-id") long candidateContactId, @PathVariable long id,
            MultipartFile file, Authentication authentication, HttpServletResponse response) throws IOException {

        pdfService.update(id, file.getBytes());
        log.info("=== PUT-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, String.format("/api/users/%d/candidate-contacts/%d", ownerId, candidateContactId));
    }

    @GetMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authPDFService.isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF" +
            "(#ownerId, #candidateContactId, #id, authentication.name)")
    public void delete(@PathVariable("owner-id") long ownerId,
                       @PathVariable("contact-id") long candidateContactId, @PathVariable long id,
                       Authentication authentication, HttpServletResponse response) {
        pdfService.delete(id);
        log.info("=== PUT-PDF_FILE === {} == {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, String.format("/api/users/%d/candidate-contacts/%d", ownerId, candidateContactId));
    }
}
