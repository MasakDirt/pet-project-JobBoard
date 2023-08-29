package com.board.job.repository;

import com.board.job.model.entity.Feedback;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class FeedbackRepositoryTests {
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackRepositoryTests(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Test
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(feedbackRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindAllByMessengerId() {
        long messengerId = 3L;

        List<Feedback> expected = feedbackRepository.findAll()
                .stream()
                .filter(feedback -> feedback.getMessengerId() == messengerId)
                .toList();

        List<Feedback> actual = feedbackRepository.findAllByMessengerId(messengerId);

        assertFalse(actual.isEmpty(),
                "We must get false because in this messenger feedbacks are available.");
        assertTrue(feedbackRepository.findAll().size() > actual.size(),
                "Actual feedbacks size must be smaller than all users");
        assertEquals(expected.size(), actual.size(),
                "Expected and actual lists must be the same because it`s read by same messenger id!");
    }

    @Test
    public void test_Invalid_FindAllByMessengerId() {
        assertTrue(feedbackRepository.findAllByMessengerId(0).isEmpty(),
                "We have no feedback with messenger id 0, so here must be empty list!");
    }
}
