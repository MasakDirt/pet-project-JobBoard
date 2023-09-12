package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.repository.candidate.CandidateContactRepository;
import com.board.job.service.PDFService;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class CandidateContactServiceTests {
    private final UserService userService;
    private final PDFService pdfService;
    private final CandidateContactService candidateContactService;
    private final CandidateContactRepository candidateContactRepository;

    @Autowired
    public CandidateContactServiceTests(UserService userService, PDFService pdfService,
                                        CandidateContactService candidateContactService,
                                        CandidateContactRepository candidateContactRepository) {
        this.userService = userService;
        this.pdfService = pdfService;
        this.candidateContactService = candidateContactService;
        this.candidateContactRepository = candidateContactRepository;
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(userService).isNotNull();
        AssertionsForClassTypes.assertThat(pdfService).isNotNull();
        AssertionsForClassTypes.assertThat(candidateContactService).isNotNull();
        AssertionsForClassTypes.assertThat(candidateContactRepository).isNotNull();
    }

    @Test
    public void test_Valid_Create() throws Exception {
        List<CandidateContact> before = candidateContactRepository.findAll();

        String pdfFilename = "files/pdf/CV_Maksym_Korniev.pdf";
        String photoFileName = "files/photos/adminPhoto.jpg";
        long ownerId = 2L;

        CandidateContact expected = new CandidateContact();
        expected.setPhone("new phone");
        expected.setTelegram("@telegram");
        expected.setLinkedInProfile("www.linkedin.co/rkgrgjropeigmpo");
        expected.setGithubUrl("www.github.co/kogrkipgjipg");
        expected.setPortfolioUrl("www.portfolio.co/LFPlepfkegkkf");
        expected.setOwner(userService.readById(ownerId));

        CandidateContact actual = candidateContactService.create(2L, expected);

        List<CandidateContact> after = candidateContactRepository.findAll();
        expected.setId(actual.getId());

        assertTrue(before.size() < after.size(),
                "List candidate contacts before creating one must be smaller than after");
        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_Create() {
        long ownerId = 2L;
        CandidateContact candidateContact = candidateContactService.readById(2L);

        assertThrows(EntityNotFoundException.class, () -> candidateContactService.create(0, candidateContact),
                "Entity not found exception will be thrown, because we have no user with id 0.");

        assertThrows(NullPointerException.class, () -> candidateContactService.create(ownerId, null),
                "Null pointer exception will be thrown, because we pass null user to method create.");

        assertThrows(ConstraintViolationException.class, () -> candidateContactService.create(ownerId, new CandidateContact()),
                "Constraint violation exception will be thrown, because we pass empty user.");
    }

    @Test
    public void test_Valid_ReadById() {
        CandidateContact expected = new CandidateContact();
        expected.setPhone("new phone");
        expected.setTelegram("@telegram");
        expected.setLinkedInProfile("www.linkedin.co/rkgrgjropeigmpo");
        expected.setGithubUrl("www.github.co/kogrkipgjipg");
        expected.setPortfolioUrl("www.portfolio.co/LFPlepfkegkkf");

        expected = candidateContactService.create(2L, expected);

        CandidateContact actual = candidateContactService.readById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> candidateContactService.readById(0),
                "Entity not found exception will be thrown because we have no candidate with id 0.");
    }

    @Test
    public void test_Valid_Update() {
        String newTelegram = "@new";
        String newName = "Charly Puer";

        CandidateContact unexpected = candidateContactService.readById(2L);
        String oldTelegram = unexpected.getTelegram();
        String oldName = unexpected.getCandidateName();

        unexpected.setTelegram(newTelegram);
        unexpected.setCandidateName(newName);

        CandidateContact actual = candidateContactService.update(unexpected.getId(), unexpected);

        assertAll(
                () -> assertEquals(unexpected.getId(), actual.getId()),
                () -> assertNotEquals(oldTelegram, actual.getTelegram()),
                () -> assertNotEquals(oldName, actual.getCandidateName())
        );
    }

    @Test
    public void test_Invalid_Update() {
        long id = 2L;
        assertThrows(NullPointerException.class, () -> candidateContactService.update(id, null),
                "Null pointer exception will be thrown, because we pass null user to method update.");

        assertThrows(EntityNotFoundException.class, () -> candidateContactService.update(0, new CandidateContact()),
                "Entity not found exception will be thrown because we have no this candidate.");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 3L;
        candidateContactService.delete(id);

        assertThrows(EntityNotFoundException.class, () -> candidateContactService.readById(id),
                "Entity not found exception will be thrown because we already delete this candidate.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> candidateContactService.delete(0),
                "Entity not found exception will be thrown because we have no candidate with id 0.");
    }
}
