package com.board.job.controller.mockito;

import com.board.job.controller.FeedbackController;
import com.board.job.model.dto.feedback.FeedbackResponse;
import com.board.job.model.dto.messenger.FullMessengerResponse;
import com.board.job.model.entity.Feedback;
import com.board.job.model.entity.Messenger;
import com.board.job.model.mapper.FeedbackMapper;
import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.FeedbackService;
import com.board.job.service.MessengerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class FeedbackControllerMockitoTests {
    @InjectMocks
    private FeedbackController controller;
    @Mock
    private FeedbackService service;
    @Mock
    private FeedbackMapper mapper;
    @Mock
    private MessengerMapper messengerMapper;
    @Mock
    private MessengerService messengerService;
    @Mock
    private Authentication authentication;

    @Test
    public void test_getAllCandidateMessengerFeedbacks() {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 10L;
        when(messengerService.readById(messengerId)).thenReturn(new Messenger());
        when(service.getAllMessengerFeedbacks(messengerId)).thenReturn(List.of());
        when(messengerMapper.getFullMessengerResponseFromMessenger(new Messenger(), List.of())).thenReturn(FullMessengerResponse.builder().build());

        controller.getAllCandidateMessengerFeedbacks(ownerId, candidateId, messengerId, authentication);

        verify(service, times(1)).getAllMessengerFeedbacks(messengerId);
        verify(messengerMapper, times(1)).getFullMessengerResponseFromMessenger(new Messenger(), List.of());
    }

    @Test
    public void test_getAllEmployerMessengerFeedbacks() {
        long ownerId = 3L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 10L;
        when(messengerService.readById(messengerId)).thenReturn(new Messenger());
        when(service.getAllMessengerFeedbacks(messengerId)).thenReturn(List.of());
        when(messengerMapper.getFullMessengerResponseFromMessenger(new Messenger(), List.of())).thenReturn(FullMessengerResponse.builder().build());

        controller.getAllEmployerMessengerFeedbacks(ownerId, employerId, vacancyId, messengerId, authentication);

        verify(service, times(1)).getAllMessengerFeedbacks(messengerId);
        verify(messengerMapper, times(1)).getFullMessengerResponseFromMessenger(new Messenger(), List.of());
    }

    @Test
    public void test_getFeedbackByCandidate() {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 10L;
        String id = "id";
        when(service.readById(id)).thenReturn(new Feedback());
        when(mapper.getFeedbackResponseFromFeedback(new Feedback())).thenReturn(FeedbackResponse.builder().build());

        controller.getFeedbackByCandidate(ownerId, candidateId, messengerId, id, authentication);

        verify(service, times(1)).readById(id);
        verify(mapper, times(1)).getFeedbackResponseFromFeedback(new Feedback());
    }

    @Test
    public void test_getFeedbackByCandidateEmployer() {
        long ownerId = 3L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 10L;
        String id = "id";

        when(service.readById(id)).thenReturn(new Feedback());
        when(mapper.getFeedbackResponseFromFeedback(new Feedback())).thenReturn(FeedbackResponse.builder().build());

        controller.getFeedbackByEmployer(ownerId, employerId, vacancyId, messengerId, id, authentication);

        verify(service, times(1)).readById(id);
        verify(mapper, times(1)).getFeedbackResponseFromFeedback(new Feedback());
    }

    @Test
    public void test_createByCandidate() {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 10L;
        String text = "Text";
        when(service.create(ownerId, messengerId, text)).thenReturn(new Feedback());

        controller.createByCandidate(ownerId, candidateId, messengerId, text, authentication);

        verify(service, times(1)).create(ownerId, messengerId, text);
    }

    @Test
    public void test_createByEmployer() {
        long ownerId = 3L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 10L;
        String text = "Text";
        when(service.create(ownerId, messengerId, text)).thenReturn(new Feedback());

        controller.createByEmployer(ownerId, employerId, vacancyId, messengerId, text, authentication);

        verify(service, times(1)).create(ownerId, messengerId, text);
    }

    @Test
    public void test_updateByCandidate() {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 10L;
        String text = "Text";
        String id = "id";
        when(service.update(id, text)).thenReturn(new Feedback());

        controller.updateByCandidate(ownerId, candidateId, messengerId, id, text, authentication);

        verify(service, times(1)).update(id, text);
    }

    @Test
    public void test_updateByEmployer() {
        long ownerId = 3L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 10L;
        String text = "Text";
        String id = "id";
        when(service.update(id, text)).thenReturn(new Feedback());

        controller.updateByEmployer(ownerId, employerId, vacancyId, messengerId, id, text, authentication);

        verify(service, times(1)).update(id, text);
    }

    @Test
    public void test_deleteByCandidate() {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 10L;
        String id = "id";

        controller.deleteByCandidate(ownerId, candidateId, messengerId, id, authentication);

        verify(service, times(1)).delete(id);
    }

    @Test
    public void test_deleteByEmployer() {
        long ownerId = 3L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 10L;
        String id = "id";

        controller.deleteByEmployer(ownerId, employerId, vacancyId, messengerId, id, authentication);

        verify(service, times(1)).delete(id);
    }
}
