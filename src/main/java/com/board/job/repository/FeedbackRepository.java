package com.board.job.repository;

import com.board.job.model.entity.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findAllByMessengerForVacanciesReplyId(long messengerId);
}
