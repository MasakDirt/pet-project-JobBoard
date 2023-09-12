package com.board.job.repository.candidate;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {
    @Query("select c from CandidateProfile c where c.salaryExpectations >= :salary")
    List<CandidateProfile> getAllWhereSalaryExpectationsBigger(@Param("salary") int salaryExpectations);

    @Query("select c from CandidateProfile c where c.salaryExpectations <= :salary")
    List<CandidateProfile> getAllWhereSalaryExpectationsSmaller(@Param("salary") int salaryExpectations);

    List<CandidateProfile> getAllByCategory(Category category);

    @Query("select c from CandidateProfile c where c.englishLevel >= :languageLevel")
    List<CandidateProfile> getAllByEnglishLevel(LanguageLevel languageLevel);

    @Query("select c from CandidateProfile c where c.workExperience >= :work")
    List<CandidateProfile> getAllByWorkExperience(@Param("work") double workExperience);

    List<CandidateProfile> getAllByCountryOfResidence(String country);

    @Query("select c from CandidateProfile c where c.salaryExpectations >= :salary and c.workExperience >= :work")
    List<CandidateProfile> getAllWhereSalaryExpectationsAndWorkExperienceBigger(
            @Param("salary") int salaryExpectations, @Param("work") double workExperience);

    @Query("select c from CandidateProfile c where c.workExperience >= :work and c.englishLevel >= :languageLevel")
    List<CandidateProfile> getAllWhereWorkExperienceAndEnglishLevelBigger(
            @Param("work") double workExperience, LanguageLevel languageLevel);

    @Query("select c from CandidateProfile c where c.workExperience >= :work and c.category = :category")
    List<CandidateProfile> getAllWhereWorkExperienceBiggerAndCategoryEqual(
            @Param("work") double workExperience, Category category);

    @Query("select c from CandidateProfile c where c.workExperience >= :work and c.countryOfResidence = :country")
    List<CandidateProfile> getAllWhereWorkExperienceBiggerAndCountryEqual(
            @Param("work") double workExperience, String country);

    @Query("select c from CandidateProfile c where c.workExperience >= :work and c.salaryExpectations >= :salary and c.countryOfResidence = :country")
    List<CandidateProfile> getAllWhereWorkExperienceAndSalaryBiggerAndCountryEqual(
            @Param("work") double workExperience, @Param("salary") int salaryExpectations, String country);

    @Query("select c from CandidateProfile c where c.workExperience >= :work and c.salaryExpectations >= :salary and c.englishLevel >= :level")
    List<CandidateProfile> getAllWhereWorkExperienceAndSalaryAndEnglishLevelBigger(
            @Param("work") double workExperience, @Param("salary") int salaryExpectations, LanguageLevel level);

    @Query("select c from CandidateProfile c where c.workExperience >= :work and c.salaryExpectations >= :salary and c.category = :category")
    List<CandidateProfile> getAllWhereWorkExperienceAndSalaryBiggerAndCategoryEqual(
            @Param("work") double workExperience, @Param("salary") int salaryExpectations, Category category);

    // etc...
}
