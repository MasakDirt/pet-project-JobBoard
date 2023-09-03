package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_contact.CandidateContactRequest;
import com.board.job.model.dto.candidate_contact.CandidateContactResponse;
import com.board.job.model.mapper.candidate.CandidateContactMapper;
import com.board.job.service.candidate.CandidateContactService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/candidate-contact")
public class CandidateContactController {
    private final CandidateContactMapper mapper;
    private final CandidateContactService candidateContactService;

    @GetMapping("/{id}")
    @PreAuthorize("@authCandidateContactService." +
            "isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts(#ownerId, #id, authentication.principal)")
    public CandidateContactResponse getCandidateById(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                                     Authentication authentication) {
        var candidateContact = mapper.getCandidateContactResponseFromCandidateContact(candidateContactService.readById(id));
        log.info("=== GET-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return candidateContact;
    }

    @PostMapping
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.principal)")
    public ResponseEntity<String> create(@PathVariable("owner-id") long ownerId, @RequestBody @Valid CandidateContactRequest request, Authentication authentication) {
        candidateContactService.create(ownerId, mapper.getCandidateContactFromCandidateContactRequest(request));
        log.info("=== POST-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Your contacts successfully recorded.");
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authCandidateContactService." +
            "isUsersSameByIdAndUserOwnerCandidateContacts(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> update(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         @RequestBody @Valid CandidateContactRequest request, Authentication authentication) {
        candidateContactService.update(id, mapper.getCandidateContactFromCandidateContactRequest(request));
        log.info("=== PUT-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok("Your contacts successfully updated.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authCandidateContactService." +
            "isUsersSameByIdAndUserOwnerCandidateContacts(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         Authentication authentication) {
        var candidateContact = candidateContactService.readById(id);
        candidateContactService.delete(id);
        log.info("=== DELETE-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("Candidate contact with name %s successfully deleted", candidateContact.getCandidateName()));
    }
}
