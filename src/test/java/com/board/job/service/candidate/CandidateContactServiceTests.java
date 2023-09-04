package com.board.job.service.candidate;

import com.board.job.model.entity.PDF_File;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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

        PDF_File pdfFile = pdfService.create(pdfFilename);

        CandidateContact expected = new CandidateContact();
        expected.setPhone("new phone");
        expected.setTelegram("@telegram");
        expected.setLinkedInProfile("www.linkedin.co/rkgrgjropeigmpo");
        expected.setGithubUrl("www.github.co/kogrkipgjipg");
        expected.setPortfolioUrl("www.portfolio.co/LFPlepfkegkkf");
        expected.setProfilePicture(Files.readAllBytes(Path.of(photoFileName)));
        expected.setPdf(pdfFile);
        expected.setOwner(userService.readById(ownerId));

        CandidateContact actual = candidateContactService.create(pdfFilename, photoFileName, 2L, expected);

        List<CandidateContact> after = candidateContactRepository.findAll();
        expected.setId(actual.getId());

        assertTrue(before.size() < after.size(),
                "List candidate contacts before creating one must be smaller than after");
        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_Create() {
        String pdfFilename = "files/pdf/CV_Maksym_Korniev.pdf";
        String photoFileName = "files/photos/adminPhoto.jpg";
        long ownerId = 2L;
        CandidateContact candidateContact = candidateContactService.readById(2L);

        assertThrows(IllegalArgumentException.class, () -> candidateContactService.create("", photoFileName, ownerId, candidateContact),
                "Illegal argument exception will be thrown, because we have no pdfFile with empty name.");

        assertThrows(IllegalArgumentException.class, () -> candidateContactService.create(pdfFilename, "", ownerId, candidateContact),
                "Illegal argument exception will be thrown, because we have no photo file with empty name.");

        assertThrows(EntityNotFoundException.class, () -> candidateContactService.create(pdfFilename, photoFileName, 0, candidateContact),
                "Entity not found exception will be thrown, because we have no user with id 0.");

        assertThrows(NullPointerException.class, () -> candidateContactService.create(pdfFilename, photoFileName, ownerId, null),
                "Null pointer exception will be thrown, because we pass null user to method create.");

        assertThrows(ConstraintViolationException.class, () -> candidateContactService.create(pdfFilename, photoFileName, ownerId, new CandidateContact()),
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

        expected = candidateContactService.create("files/pdf/CV_Maksym_Korniev.pdf", "files/photos/violetPhoto.jpg",
                2L, expected);

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

        CandidateContact actual = candidateContactService.update(unexpected);

        assertAll(
                () -> assertEquals(unexpected.getId(), actual.getId()),
                () -> assertNotEquals(oldTelegram, actual.getTelegram()),
                () -> assertNotEquals(oldName, actual.getCandidateName())
        );
    }

    @Test
    public void test_Invalid_Update() {
        assertThrows(NullPointerException.class, () -> candidateContactService.update(null),
                "Null pointer exception will be thrown, because we pass null user to method update.");

        assertThrows(EntityNotFoundException.class, () -> candidateContactService.update(new CandidateContact()),
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

    @Test
    public void test_Valid_GetWithPropertyPictureAttachedById() {
        long id = 3L;
        byte[] expected = candidateContactRepository.findWithPropertyPictureAttachedById(id);

        byte[] actual = candidateContactService.getWithPropertyPictureAttachedById(id);

        assertEquals(Arrays.toString(expected), Arrays.toString(actual),
                "We read bytes be same id, so they must be equal.");
    }

    @Test
    public void test_Invalid_GetWithPropertyPictureAttachedById() {
        assertEquals(Arrays.toString(new byte[0]), Arrays.toString(candidateContactService.getWithPropertyPictureAttachedById(0)),
                "Arrays must be empty because we hve no user with this id.");
    }

    @Test
    public void test_Valid_SaveProfilePicture() throws Exception {
        long id = 3L;
        CandidateContact candidateContact = candidateContactService.readById(id);
        byte[] unexpected = candidateContact.getProfilePicture();

        candidateContactService.saveProfilePicture(id, Files.readAllBytes(Path.of("files/photos/nicolas.jpg")));

        byte[] actual = candidateContact.getProfilePicture();

        assertNotEquals(Arrays.toString(unexpected), Arrays.toString(actual),
                "We added new profile picture so 'unexpected' array must be empty");
    }

    @Test
    public void test_Invalid_SaveProfilePicture() {
        assertThrows(EntityNotFoundException.class, () -> candidateContactService.saveProfilePicture(
                        0, Files.readAllBytes(Path.of("files/photos/nicolas.jpg"))),
                "Entity not found exception will be thrown because we have no employer profile with id 0.");
    }
}