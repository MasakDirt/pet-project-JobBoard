package com.board.job.model.mapper;

import com.board.job.model.dto.feedback.FeedbackResponse;
import com.board.job.model.dto.messenger.CutMessengerResponse;
import com.board.job.model.dto.messenger.FullMessengerResponse;
import com.board.job.model.entity.Feedback;
import com.board.job.model.entity.MessengerForVacanciesReply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessengerMapper {

    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    @Mapping(target = "country", expression = "java(messenger.getVacancy().getCountry())")
    @Mapping(target = "postedAt", expression = "java(messenger.getVacancy().getPostedAt())")
    @Mapping(target = "category", expression = "java(messenger.getVacancy().getCategory())")
    @Mapping(target = "lookingFor", expression = "java(messenger.getVacancy().getLookingFor())")
    @Mapping(target = "employerName", expression = "java(messenger.getVacancy().getEmployerProfile().getEmployerName())")
    @Mapping(target = "lastMessage", source = "lastMessage")
    CutMessengerResponse getCutMessengerResponseFromMessenger(MessengerForVacanciesReply messenger, String lastMessage);

    @Mapping(target = "vacancy", expression = "java(messenger.getVacancy())")
    @Mapping(target = "feedbacks", expression = "java(refactorMessages(feedbacks))")
    @Mapping(target = "country", expression = "java(messenger.getVacancy().getCountry())")
    @Mapping(target = "postedAt", expression = "java(messenger.getVacancy().getPostedAt())")
    @Mapping(target = "category", expression = "java(messenger.getVacancy().getCategory())")
    @Mapping(target = "lookingFor", expression = "java(messenger.getVacancy().getLookingFor())")
    @Mapping(target = "companyName", expression = "java(messenger.getVacancy().getEmployerProfile().getCompanyName())")
    @Mapping(target = "employerName", expression = "java(messenger.getVacancy().getEmployerProfile().getEmployerName())")
    FullMessengerResponse getFullMessengerResponseFromMessenger(MessengerForVacanciesReply messenger, List<Feedback> feedbacks);

    default List<FeedbackResponse> refactorMessages(List<Feedback> feedbacks) {
        return feedbacks
                .stream()
                .map(INSTANCE::getFeedbackResponseFromFeedback)
                .toList();
    }
}
