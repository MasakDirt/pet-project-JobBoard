package com.board.job.repository.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    @Query("SELECT e FROM EmployerProfile e LEFT JOIN FETCH e.profilePicture WHERE e.id = :id")
    Optional<EmployerProfile> findWithPropertyPictureAttachedById(@Param("id") long id);
}
