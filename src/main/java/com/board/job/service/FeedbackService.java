package com.board.job.service;

import com.board.job.model.entity.Feedback;
import com.board.job.repository.FeedbackRepository;
import com.google.common.collect.Iterables;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public Feedback create(long ownerId, long messengerForVacancyReplyId, String text) {
        var feedback = new Feedback();
        feedback.setText(text);
        feedback.setOwnerId(ownerId);
        feedback.setMessengerId(messengerForVacancyReplyId);

        return feedbackRepository.save(feedback);
    }

    public Feedback readById(String id) {
        return feedbackRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Feedback not found"));
    }

    public Feedback update(String id, String text) {
        var feedback = readById(id);
        feedback.setText(text);

        return feedbackRepository.save(feedback);
    }

    public void delete(String id) {
        feedbackRepository.delete(readById(id));
    }

    public List<Feedback> getAllVacancyMessengerFeedbacks(long messengerId) {
        return feedbackRepository.findAllByMessengerId(messengerId);
    }

    public String getLastFeedbackText(long messengerId) {
        List<Feedback> feedbacks = getAllVacancyMessengerFeedbacks(messengerId)
                .stream()
                .sorted(Comparator.comparing(Feedback::getSendAt))
                .toList();

        return feedbacks.isEmpty() ? "" : Iterables.getLast(feedbacks).getText();
    }
}
