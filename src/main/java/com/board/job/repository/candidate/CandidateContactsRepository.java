package com.board.job.repository.candidate;

import com.board.job.model.entity.candidate.CandidateContacts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateContactsRepository extends JpaRepository<CandidateContacts, Long> {
}
