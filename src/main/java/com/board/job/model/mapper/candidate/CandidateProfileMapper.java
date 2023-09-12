package com.board.job.model.mapper.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
import com.board.job.model.dto.candidate_profile.CutCandidateProfileResponse;
import com.board.job.model.dto.candidate_profile.FullCandidateProfileResponse;
import com.board.job.model.entity.candidate.CandidateProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateProfileMapper {
    FullCandidateProfileResponse getCandidateProfileResponseFromCandidateProfile(CandidateProfile candidateProfile);

    CutCandidateProfileResponse getCutCandidateProfileResponseFromCandidateProfile(CandidateProfile candidateProfile);

    CandidateProfile getCandidateProfileFromCandidateProfileRequest(CandidateProfileRequest request);
}
