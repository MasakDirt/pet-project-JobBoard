package com.board.job.repository.candidate;

import com.board.job.model.entity.candidate.CandidateContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateContactsRepository extends JpaRepository<CandidateContacts, Long> {
    @Query("SELECT c FROM CandidateContacts c LEFT JOIN FETCH c.profilePicture WHERE c.id = :id")
    CandidateContacts findWithPropertyPictureAttachedById(@Param("id") long id);
}
