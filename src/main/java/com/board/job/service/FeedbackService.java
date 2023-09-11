package com.board.job.service;

import com.board.job.model.entity.Feedback;
import com.board.job.repository.FeedbackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public Feedback create(long ownerId, long messengerId, String text) {
        var feedback = new Feedback();
        feedback.setText(text);
        feedback.setOwnerId(ownerId);
        feedback.setMessengerId(messengerId);

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

    public List<Feedback> getAllMessengerFeedbacks(long messengerId) {
        return feedbackRepository.findAllByMessengerId(messengerId)
                .stream()
                .sorted(((o1, o2) -> o2.getSendAt().compareTo(o1.getSendAt())))
                .toList();
    }
}
