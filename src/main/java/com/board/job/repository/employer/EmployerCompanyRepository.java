package com.board.job.repository.employer;

import com.board.job.model.entity.employer.EmployerCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerCompanyRepository extends JpaRepository<EmployerCompany, Long> {
}
