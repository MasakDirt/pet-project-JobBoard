package com.board.job.service.mockito;

import com.board.job.model.entity.Feedback;
import com.board.job.repository.FeedbackRepository;
import com.board.job.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class FeedbackServiceMockitoTests {
    @InjectMocks
    private FeedbackService feedbackService;
    @Mock
    private FeedbackRepository feedbackRepository;

    @Test
    public void test_Create() {
        long ownerId = 4L;
        long messengerId = 2L;
        String text = "Text";

        Feedback feedback = new Feedback();
        feedback.setText(text);
        feedback.setMessengerId(messengerId);
        when(feedbackRepository.save(feedback)).thenReturn(feedback);

        Feedback actual = feedbackService.create(ownerId, messengerId, text);

        assertEquals(feedback, actual);
    }

    @Test
    public void test_ReadById() {
        String id = "ID";
        Feedback feedback = new Feedback();
        feedback.setText("new text");

        when(feedbackRepository.findById(id)).thenReturn(Optional.of(feedback));
        Feedback actual = feedbackService.readById(id);

        verify(feedbackRepository, times(1)).findById(id);

        assertEquals(feedback, actual);
    }

    @Test
    public void test_Update() {
        Feedback feedback = new Feedback();
        feedback.setText("new text");

        when(feedbackRepository.findById("feedback")).thenReturn(Optional.of(feedback));
        Feedback actual = feedbackService.update("feedback", "new text");

        verify(feedbackRepository, times(1)).save(feedback);

        assertNotEquals(feedback, actual);
    }

    @Test
    public void test_Delete() {
        when(feedbackRepository.findById("5L")).thenReturn(Optional.of(new Feedback()));
        feedbackService.delete("5L");

        verify(feedbackRepository, times(1)).delete(new Feedback());
    }

    @Test
    public void test_getAllMessengerFeedbacks() {
        List<Feedback> expected = List.of(new Feedback(), new Feedback());
        when(feedbackRepository.findAllByMessengerId(2L)).thenReturn(expected);
        List<Feedback> actual = feedbackService.getAllMessengerFeedbacks(2L);

        verify(feedbackRepository, times(1)).findAllByMessengerId(2L);

        assertEquals(expected, actual);
    }
}
