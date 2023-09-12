package com.board.job.model.mapper;

import com.board.job.model.dto.feedback.FeedbackResponse;
import com.board.job.model.entity.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackResponse getFeedbackResponseFromFeedback(Feedback feedback);
}
