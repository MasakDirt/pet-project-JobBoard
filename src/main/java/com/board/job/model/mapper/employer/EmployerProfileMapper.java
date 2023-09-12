package com.board.job.model.mapper.employer;

import com.board.job.model.dto.employer_profile.EmployerProfileRequest;
import com.board.job.model.dto.employer_profile.EmployerProfileResponse;
import com.board.job.model.entity.employer.EmployerProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployerProfileMapper {
    EmployerProfileResponse getEmployerProfileResponseFromEmployerProfile(EmployerProfile employerProfile);

    EmployerProfile getEmployerProfileFromEmployerProfileRequest(EmployerProfileRequest employerProfileRequest);
}
