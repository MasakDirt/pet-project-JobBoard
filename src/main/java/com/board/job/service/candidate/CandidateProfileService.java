package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.repository.candidate.CandidateProfileRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.*;

@Service
@AllArgsConstructor
public class CandidateProfileService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserService userService;

    public CandidateProfile create(long ownerId, CandidateProfile candidateProfile) {
        candidateProfile.setOwner(
                userService.updateUserRolesAndGetUser(ownerId, "CANDIDATE")
        );

        return candidateProfileRepository.save(candidateProfile);
    }

    public CandidateProfile readById(long id) {
        return candidateProfileRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("CandidateProfile not found"));
    }

    public CandidateProfile update(long id, CandidateProfile updated) {
        var old = readById(id);
        updated.setId(id);
        updated.setOwner(old.getOwner());
        updated.setMessengers(old.getMessengers());
        return candidateProfileRepository.save(updated);
    }

    public void delete(long id) {
        candidateProfileRepository.delete(readById(id));
    }

    public List<CandidateProfile> getAll() {
        return candidateProfileRepository.findAll(sort(CandidateProfile.class));
    }

    public List<CandidateProfile> getAllByASC(String property) {
        return candidateProfileRepository.findAll(by(Order.asc(property)));
    }

    public List<CandidateProfile> getAllByDESC(String property) {
        return candidateProfileRepository.findAll(by(Order.desc(property)));
    }

    public List<CandidateProfile> getAllBiggerSalaryExpectation(int salaryExpectation) {
        return candidateProfileRepository.getAllWhereSalaryExpectationsBigger(salaryExpectation);
    }

    public List<CandidateProfile> getAllSmallerSalaryExpectation(int salaryExpectation) {
        return candidateProfileRepository.getAllWhereSalaryExpectationsSmaller(salaryExpectation);
    }

    public List<CandidateProfile> getAllByCategory(String categoryName) {
        return candidateProfileRepository.getAllByCategory(Category.valueOf(categoryName.toUpperCase()));
    }

    public List<CandidateProfile> getAllByEnglishLevel(String levelName) {
        return candidateProfileRepository.getAllByEnglishLevel(LanguageLevel.valueOf(levelName.toUpperCase()));
    }

    public List<CandidateProfile> getAllByWorkExperience(double workExperience) {
        return candidateProfileRepository.getAllByWorkExperience(workExperience);
    }

    public List<CandidateProfile> getAllByCountryOfResidence(String country) {
        return candidateProfileRepository.getAllByCountryOfResidence(country);
    }

    public List<CandidateProfile> getAllBiggerSalaryExpectationAndWorkExperience(int salaryExpectation, double workExperience) {
        return candidateProfileRepository.getAllWhereSalaryExpectationsAndWorkExperienceBigger(salaryExpectation, workExperience);
    }

    public List<CandidateProfile> getAllWhereWorkExperienceAndEnglishLevelBigger(double workExperience, String level) {
        return candidateProfileRepository.getAllWhereWorkExperienceAndEnglishLevelBigger(workExperience,
                LanguageLevel.valueOf(level.toUpperCase()));
    }

    public List<CandidateProfile> getAllWhereWorkExperienceBiggerAndCategoryEqual(double workExperience, String name) {
        return candidateProfileRepository.getAllWhereWorkExperienceBiggerAndCategoryEqual(workExperience,
                Category.valueOf(name.toUpperCase()));
    }

    public List<CandidateProfile> getAllWhereWorkExperienceBiggerAndCountryEqual(double workExperience, String country) {
        return candidateProfileRepository.getAllWhereWorkExperienceBiggerAndCountryEqual(workExperience,
                country);
    }

    public List<CandidateProfile> getAllWhereWorkExperienceAndSalaryBiggerAndCountryEqual(
            double workExperience, int salaryExpectations, String country) {

        return candidateProfileRepository.getAllWhereWorkExperienceAndSalaryBiggerAndCountryEqual(
                workExperience, salaryExpectations, country);
    }

    public List<CandidateProfile> getAllWhereWorkExperienceAndSalaryAndEnglishLevelBigger(
            double workExperience, int salaryExpectations, String level) {

        return candidateProfileRepository.getAllWhereWorkExperienceAndSalaryAndEnglishLevelBigger(
                workExperience, salaryExpectations, LanguageLevel.valueOf(level.toUpperCase()));
    }

    public List<CandidateProfile> getAllWhereWorkExperienceAndSalaryBiggerAndCategoryEqual(
            double workExperience, int salaryExpectations, String category) {

        return candidateProfileRepository.getAllWhereWorkExperienceAndSalaryBiggerAndCategoryEqual(
                workExperience, salaryExpectations, Category.valueOf(category.toUpperCase()));
    }
}
