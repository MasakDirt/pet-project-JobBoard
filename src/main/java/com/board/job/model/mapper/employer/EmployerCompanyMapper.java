package com.board.job.model.mapper.employer;

import com.board.job.model.dto.employer_company.EmployerCompanyResponse;
import com.board.job.model.entity.employer.EmployerCompany;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployerCompanyMapper {
    EmployerCompanyResponse getEmployerCompanyResponseFromEmployerCompany(EmployerCompany employerCompany);
}
