package com.board.job.model.mapper.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileResponse;
import com.board.job.model.entity.candidate.CandidateProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateProfileMapper {
    CandidateProfileResponse getCandidateProfileResponseFromCandidateProfile(CandidateProfile candidateProfile);
}
