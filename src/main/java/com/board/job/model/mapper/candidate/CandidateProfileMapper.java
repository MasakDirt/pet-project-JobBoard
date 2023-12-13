package com.board.job.model.mapper.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
import com.board.job.model.dto.candidate_profile.CandidateProfileResponse;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {Category.class, LanguageLevel.class})
public interface CandidateProfileMapper {

    @Mapping(target = "category", expression = "java(candidateProfile.getCategory().getValue())")
    @Mapping(target = "englishLevel", expression = "java(candidateProfile.getEnglishLevel().getValue())")
    @Mapping(target = "ukrainianLevel", expression = "java(candidateProfile.getUkrainianLevel().getValue())")
    @Mapping(target = "owner", expression = "java(candidateProfile.getOwner())")
    CandidateProfileResponse getCandidateProfileResponseFromCandidateProfile(CandidateProfile candidateProfile);

    @Mapping(target = "category", expression = "java(Category.checkValuesAndGet(request.getCategory()))")
    @Mapping(target = "englishLevel", expression = "java(LanguageLevel.checkValuesAndGet(request.getEnglishLevel()))")
    @Mapping(target = "ukrainianLevel", expression = "java(LanguageLevel.checkValuesAndGet(request.getUkrainianLevel()))")
    CandidateProfile getCandidateProfileFromCandidateProfileRequest(CandidateProfileRequest request);
}
