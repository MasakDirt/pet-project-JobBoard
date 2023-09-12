package com.board.job.repository.candidate;

import com.board.job.model.entity.candidate.CandidateContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateContactRepository extends JpaRepository<CandidateContact, Long> {
}
