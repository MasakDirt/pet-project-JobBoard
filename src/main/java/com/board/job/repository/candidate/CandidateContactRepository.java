package com.board.job.repository.candidate;

import com.board.job.model.entity.candidate.CandidateContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateContactRepository extends JpaRepository<CandidateContact, Long> {
    @Query("SELECT c.profilePicture FROM CandidateContact c WHERE c.id = :id")
    byte[] findWithPropertyPictureAttachedById(@Param("id") long id);
}
