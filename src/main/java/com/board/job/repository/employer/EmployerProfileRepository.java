package com.board.job.repository.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    @Query("SELECT e.profilePicture FROM EmployerProfile e WHERE e.id = :id")
    byte[] findWithPropertyPictureAttachedById(@Param("id") long id);
}
