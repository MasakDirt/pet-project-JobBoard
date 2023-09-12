package com.board.job.service;

import com.board.job.model.entity.Feedback;
import com.board.job.repository.FeedbackRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class FeedbackServiceTests {
    private final FeedbackService feedbackService;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceTests(FeedbackService feedbackService, FeedbackRepository feedbackRepository) {
        this.feedbackService = feedbackService;
        this.feedbackRepository = feedbackRepository;
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(feedbackService).isNotNull();
        AssertionsForClassTypes.assertThat(feedbackRepository).isNotNull();
    }

    @Test
    public void test_Valid_Create() {
        long ownerId = 1L;
        long messengerId = 2L;
        String text = "text";
        List<Feedback> before = feedbackService.getAllMessengerFeedbacks(messengerId);

        Feedback expected = new Feedback();
        expected.setText(text);
        expected.setMessengerId(messengerId);
        expected.setOwnerId(ownerId);

        Feedback actual = feedbackService.create(ownerId, messengerId, text);
        expected.setId(actual.getId());
        List<Feedback> after = feedbackService.getAllMessengerFeedbacks(messengerId);

        assertTrue(before.size() < after.size(),
                "Before creating feedback list must be smaller than after.");

        assertEquals(expected, actual,
                "Feedbacks has sames values, so they must be equal!");
    }

    @Test
    public void test_Valid_ReadById() {
        Feedback expected = feedbackService.create(2L, 3L, "Feedback");

        Feedback actual = feedbackService.readById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> feedbackService.readById(""),
                "Entity not found exception will be thrown, because we have not feedback with empty id");
    }

    @Test
    public void test_Valid_Update() {
        String oldText = "Old text";
        String newText = "New text";

        Feedback unexpected = feedbackService.create(1L, 3L,  oldText);

        Feedback actual = feedbackService.update(unexpected.getId(), newText);

        assertNotEquals(unexpected, actual,
                "After updating they must be sames");

        assertEquals(actual.getText(), newText,
                "New text must equals.");

        assertEquals(unexpected.getId(), actual.getId(),
                "Id`s we do not update so they must be equal.");
    }

    @Test
    public void test_Invalid_Update() {
        assertThrows(EntityNotFoundException.class, () -> feedbackService.update("", "text"),
                "Entity not found exception will be thrown, because we have not feedback with empty id");
    }

    @Test
    public void test_Valid_Delete() {
        Feedback delete = feedbackService.create(3L, 1L, "Text");

        feedbackService.delete(delete.getId());

        assertThrows(EntityNotFoundException.class, () -> feedbackService.readById(delete.getId()),
                "After deleting we have no this entity in db, so here must thrown entity not found exception");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> feedbackService.delete(""),
                "Entity not found exception will be thrown, because we have not feedback with empty id");
    }

    @Test
    public void test_Valid_GetAllCandidateFeedbacks() {
        long messengerId = 3L;
        List<Feedback> expected = feedbackRepository.findAll()
                .stream()
                .filter(feedback -> feedback.getMessengerId() == messengerId)
                .toList();

        List<Feedback> actual = feedbackService.getAllMessengerFeedbacks(messengerId);

        assertEquals(expected, actual,
                "Lists must be same");
    }
}
