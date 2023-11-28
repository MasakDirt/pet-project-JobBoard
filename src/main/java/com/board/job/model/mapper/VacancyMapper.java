package com.board.job.model.mapper;

import com.board.job.model.dto.vacancy.CutVacancyResponse;
import com.board.job.model.dto.vacancy.FullVacancyResponse;
import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, DateTimeFormatter.class, WorkMode.class,
        JobDomain.class, Category.class, LanguageLevel.class})
public interface VacancyMapper {
    @Mapping(target = "id", expression = "java(vacancy.getId())")
    @Mapping(target = "postedAt", expression = "java(vacancy.getPostedAt().format(DateTimeFormatter.ofPattern(\"dd MMM\")))")
    @Mapping(target = "employerProfile", expression = "java(vacancy.getEmployerProfile())")
    CutVacancyResponse getCutVacancyResponseFromVacancy(Vacancy vacancy);

    @Mapping(target = "postedAt", expression = "java(vacancy.getPostedAt().format(DateTimeFormatter.ofPattern(\"dd MMM\")))")
    FullVacancyResponse getFullVacancyResponseFromVacancy(Vacancy vacancy);

    @Mapping(target = "domain", expression = "java(vacancy.getDomain().getValue())")
    @Mapping(target = "category", expression = "java(vacancy.getCategory().getValue())")
    @Mapping(target = "workMode", expression = "java(vacancy.getWorkMode().getValue())")
    @Mapping(target = "englishLevel", expression = "java(vacancy.getEnglishLevel().getValue())")
    VacancyRequest getVacancyRequestFromVacancy(Vacancy vacancy);

    @Mapping(target = "postedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "domain", expression = "java(JobDomain.checkValuesAndGet(vacancyRequest.getDomain()))")
    @Mapping(target = "category", expression = "java(Category.checkValuesAndGet(vacancyRequest.getCategory()))")
    @Mapping(target = "workMode", expression = "java(WorkMode.checkValuesAndGet(vacancyRequest.getWorkMode()))")
    @Mapping(target = "englishLevel", expression = "java(LanguageLevel.checkValuesAndGet(vacancyRequest.getEnglishLevel()))")
    Vacancy getVacancyFromVacancyRequest(VacancyRequest vacancyRequest);
}
