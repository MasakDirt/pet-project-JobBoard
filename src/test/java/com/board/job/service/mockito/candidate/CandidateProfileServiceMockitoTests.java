package com.board.job.service.mockito.candidate;

import com.board.job.model.entity.User;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.repository.candidate.CandidateProfileRepository;
import com.board.job.service.UserService;
import com.board.job.service.candidate.CandidateProfileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CandidateProfileServiceMockitoTests {
    @InjectMocks
    private CandidateProfileService candidateProfileService;
    @Mock
    private CandidateProfileRepository candidateProfileRepository;
    @Mock
    private UserService userService;

    @Test
    public void test_Create() {
        long ownerID = 2L;
        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setCategory(Category.C_PLUS_PLUS);
        candidateProfile.setExperienceExplanation("Experience");

        when(userService.updateUserRolesAndGetUser(ownerID, "CANDIDATE")).thenReturn(new User());
        when(candidateProfileRepository.save(candidateProfile)).thenReturn(candidateProfile);
        CandidateProfile actual = candidateProfileService.create(ownerID, candidateProfile);

        verify(candidateProfileRepository, times(1)).save(candidateProfile);

        assertEquals(candidateProfile, actual);
    }

    @Test
    public void test_ReadById() {
        long id = 2L;
        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setPosition("new position");

        when(candidateProfileRepository.findById(id)).thenReturn(Optional.of(candidateProfile));
        CandidateProfile actual = candidateProfileService.readById(id);

        verify(candidateProfileRepository, times(1)).findById(id);

        assertEquals(candidateProfile, actual);
    }

    @Test
    public void test_Update() {
        long id = 3L;
        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setEnglishLevel(LanguageLevel.ADVANCED_FLUENT);
        candidateProfile.setId(id);

        when(candidateProfileRepository.findById(id)).thenReturn(Optional.of(candidateProfile));
        when(candidateProfileRepository.save(candidateProfile)).thenReturn(candidateProfile);

        CandidateProfile actual = candidateProfileService.update(candidateProfile);
        verify(candidateProfileRepository, times(1)).save(candidateProfile);

        Assertions.assertEquals(candidateProfile, actual);
    }

    @Test
    public void test_Delete() {
        when(candidateProfileRepository.findById(5L)).thenReturn(Optional.of(new CandidateProfile()));
        candidateProfileService.delete(5L);

        verify(candidateProfileRepository, times(1)).delete(new CandidateProfile());
    }

    @Test
    public void test_GetAll() {
        List<CandidateProfile> expected = List.of(new CandidateProfile(), new CandidateProfile());
        when(candidateProfileRepository.findAll()).thenReturn(expected);
        List<CandidateProfile> actual = candidateProfileService.getAll();

        verify(candidateProfileRepository, times(1)).findAll();

        assertEquals(expected, actual);
    }
}
