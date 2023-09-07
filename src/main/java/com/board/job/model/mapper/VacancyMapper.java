package com.board.job.model.mapper;

import com.board.job.model.dto.vacancy.CutVacancyResponse;
import com.board.job.model.dto.vacancy.FullVacancyResponse;
import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.entity.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacancyMapper {
    @Mapping(target = "id", expression = "java(vacancy.getId())")
    CutVacancyResponse getCutVacancyResponseFromVacancy(Vacancy vacancy);

    FullVacancyResponse getFullVacancyResponseFromVacancy(Vacancy vacancy);

    Vacancy getVacancyFromVacancyRequest(VacancyRequest vacancyRequest);
}
