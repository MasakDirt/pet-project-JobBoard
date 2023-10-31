package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_contact.CandidateContactRequest;
import com.board.job.model.mapper.candidate.CandidateContactMapper;
import com.board.job.service.UserService;
import com.board.job.service.candidate.CandidateContactService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/candidate-contacts")
public class CandidateContactController {
    private final CandidateContactMapper mapper;
    private final UserService userService;
    private final CandidateContactService candidateContactService;

    @GetMapping("/{id}")
    @PreAuthorize("@authCandidateContactService." +
            "isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts(#ownerId, #id, authentication.name)")
    public ModelAndView getCandidateById(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         Authentication authentication, ModelMap map) {
        log.info("=== GET-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getName());
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("candidateContactRequest", mapper
                .getCandidateContactRequestFromCandidateContact(candidateContactService.readById(id)));

        return new ModelAndView("candidate-contact-get", map);
    }

    @GetMapping("/create")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.name)")
    public ModelAndView createRequest(@PathVariable("owner-id") long ownerId, ModelMap map) {
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("candidateContactRequest", new CandidateContactRequest());

        return new ModelAndView("candidate-contact-create", map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.name)")
    public void create(@PathVariable("owner-id") long ownerId, @Valid CandidateContactRequest request,
                       Authentication authentication, HttpServletResponse response) throws IOException {
        var created = candidateContactService.create(ownerId, mapper.getCandidateContactFromCandidateContactRequest(request));
        log.info("=== POST-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%d/candidate-contacts/%d", ownerId, created.getId()));
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("@authCandidateContactService." +
            "isUsersSameByIdAndUserOwnerCandidateContacts(#ownerId, #id, authentication.name)")
    public void update(@PathVariable("owner-id") long ownerId, @PathVariable long id, @Valid CandidateContactRequest request,
                       Authentication authentication, HttpServletResponse response) throws Exception {

        candidateContactService.update(id, mapper.getCandidateContactFromCandidateContactRequest(request));
        log.info("=== PUT-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%d/candidate-contacts/%d", ownerId, id));
    }

    @GetMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authCandidateContactService." +
            "isUsersSameByIdAndUserOwnerCandidateContacts(#ownerId, #id, authentication.name)")
    public void delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         Authentication authentication, HttpServletResponse response) throws IOException {
        candidateContactService.delete(id);
        log.info("=== DELETE-CANDIDATE_CONTACT === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect("/api/users/" + ownerId);
    }
}
