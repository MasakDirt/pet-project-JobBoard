package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_contact.CandidateContactRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.board.job.helper.HelperForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CandidateContactControllerTests {
    private final static String BASIC_URl = "/api/users/{owner-id}/candidate-contacts";

    private final MockMvc mvc;

    @Autowired
    public CandidateContactControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void testInjectedComponent() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetCandidateByIdADMIN() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, candidateId))
                .andExpect(model().attributeExists("owner", "candidateContactRequest"))
                .andExpect(view().name("candidates/candidate-contact-get"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testValidGetCandidateByIdUSER() throws Exception {
        long ownerId = 4L;
        long candidateId = 3L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, candidateId))
                .andExpect(model().attributeExists("owner", "candidateContactRequest"))
                .andExpect(view().name("candidates/candidate-contact-get"));
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenGetCandidateByIdUSER() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, candidateId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    public void testNotFoundGetCandidateByIdADMIN() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}", 100L, 10L))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testGetCreateRequest() throws Exception {
        long ownerId = 4L;
        long candidateId = 3L;

        mvc.perform(get(BASIC_URl + "/create", ownerId, candidateId))
                .andExpect(model().attributeExists("owner", "candidateContactRequest"))
                .andExpect(view().name("candidates/candidate-contact-create"));
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testCreate() throws Exception {
        long ownerId = 6L;

        mvc.perform(post(BASIC_URl, ownerId)
                        .param("candidateName", "New")
                        .param("email", "email@mail.co")
                        .param("phone", "00489344355")
                        .param("githubUrl", "www.github.co/23424")
                        .param("telegram", "@tele")
                        .param("portfolioUrl", "www.portfolio.co/eorRIEOFk")
                        .param("linkedInProfile", "www.linkedin.co/OKFOkdld")
        )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/candidate-contacts/%d", ownerId, 8L)));
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testBadRequestCreateUSER() throws Exception {
        long ownerID = 3L;
        CandidateContactRequest candidateContactRequest = CandidateContactRequest.builder().build();
        candidateContactRequest.setCandidateName("New");
        candidateContactRequest.setEmail("email@mail.co");

        mvc.perform(post(BASIC_URl, ownerID)
                        .param("candidateName", "New")
                        .param("email", "email@mail.co")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testForbiddenCreateUSER() throws Exception {
        long ownerID = 2L;

        mvc.perform(post(BASIC_URl, ownerID)
                        .param("candidateName", "New")
                        .param("email", "email@mail.co")
                        .param("phone", "00489344355")
                        .param("githubUrl", "www.github.co/23424")
                        .param("telegram", "@tele")
                        .param("portfolioUrl", "www.portfolio.co/eorRIEOFk")
                        .param("linkedInProfile", "www.linkedin.co/OKFOkdld")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testUpdateUSER() throws Exception {
        long ownerID = 4L;
        long candidateID = 3L;

        mvc.perform(post(BASIC_URl + "/{id}/update", ownerID, candidateID)
                        .param("candidateName", "New")
                        .param("email", "email@mail.co")
                        .param("phone", "00489344355")
                        .param("githubUrl", "www.github.co/23424")
                        .param("telegram", "@tele")
                        .param("portfolioUrl", "www.portfolio.co/eorRIEOFk")
                        .param("linkedInProfile", "www.linkedin.co/OKFOkdld")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/candidate-contacts/%d", ownerID, candidateID)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testForbiddenUpdateADMIN() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(post(BASIC_URl + "/{id}/update", ownerID, candidateID))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void testDeleteUSER() throws Exception {
        long ownerID = 3L;
        long candidateID = 2L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", ownerID, candidateID))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/users/" + ownerID));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenDeleteUSER() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", ownerID, candidateID))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
