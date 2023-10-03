package com.board.job.controller.mockito.candidate;

import com.board.job.controller.candidate.CandidateProfileController;
import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
import com.board.job.model.dto.candidate_profile.CutCandidateProfileResponse;
import com.board.job.model.dto.candidate_profile.FullCandidateProfileResponse;
import com.board.job.model.entity.User;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.mapper.candidate.CandidateProfileMapper;
import com.board.job.service.candidate.CandidateProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CandidateProfileControllerMockitoTests {
    @InjectMocks
    private CandidateProfileController controller;
    @Mock
    private CandidateProfileService service;
    @Mock
    private CandidateProfileMapper mapper;
    @Mock
    private Authentication authentication;

    @Test
    public void test_GetAll() {
        when(service.getAll()).thenReturn(List.of(new CandidateProfile(), new CandidateProfile()));
        when(mapper.getCutCandidateProfileResponseFromCandidateProfile(new CandidateProfile()))
                .thenReturn(CutCandidateProfileResponse.builder().build());
        controller.getAll(authentication);

        verify(service, times(1)).getAll();
    }

    @Test
    public void test_getSortedVacancies() {
        Sort sort = Sort.by(Sort.Direction.fromString("asc"), "id");
        Pageable pageable = PageRequest.of(1, 5, sort);

        when(service.getAllSorted(pageable)).thenReturn(Page.empty());
        controller.getSortedVacancies(new String[]{"id"}, "asc", 1, authentication);

        verify(service, times(1)).getAllSorted(pageable);
    }

    @Test
    public void test_GetById() {
        long ownerId = 34945L;
        long id = 345L;

        when(mapper.getCandidateProfileResponseFromCandidateProfile(new CandidateProfile())).thenReturn(FullCandidateProfileResponse.builder().build());
        when(service.readById(id)).thenReturn(new CandidateProfile());
        controller.getById(ownerId, id, authentication);

        verify(service, times(1)).readById(id);
    }

    @Test
    public void test_NotFound_getById() {
        long ownerId = 34945L;
        long id = 100L;
        when(service.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getById(ownerId, id, authentication));

        verify(service, times(1)).readById(id);
        verify(mapper, times(0)).getCandidateProfileResponseFromCandidateProfile(new CandidateProfile());
    }

    @Test
    public void test_Create() {
        long ownerId = 3L;
        User user = new User();
        user.setFirstName("First");
        user.setLastName("Last");

        CandidateProfile profile = new CandidateProfile();
        profile.setId(2L);
        profile.setPosition("Java dev");
        profile.setCategory(Category.JAVA);
        profile.setOwner(user);

        when(mapper.getCandidateProfileFromCandidateProfileRequest(CandidateProfileRequest.builder().build())).thenReturn(profile);
        when(service.create(ownerId, profile)).thenReturn(profile);
        controller.create(ownerId, CandidateProfileRequest.builder().build(), authentication);

        verify(service, times(1)).create(ownerId, profile);
    }

    @Test
    public void test_Update() {
        long ownerId = 3L;
        long id = 3L;
        User user = new User();
        user.setFirstName("First");
        user.setLastName("Last");

        CandidateProfile profile = new CandidateProfile();
        profile.setId(id);
        profile.setPosition("Java dev");
        profile.setCategory(Category.JAVA);
        profile.setOwner(user);

        when(mapper.getCandidateProfileFromCandidateProfileRequest(CandidateProfileRequest.builder().build())).thenReturn(profile);
        when(service.update(id, profile)).thenReturn(profile);
        controller.update(ownerId, id,
                CandidateProfileRequest.builder().build(), authentication);

        verify(service, times(1)).update(id, profile);
    }

    @Test
    public void test_Delete() {
        long ownerId = 3L;
        long id = 3L;
        User user = new User();
        user.setFirstName("First");
        user.setLastName("Last");

        CandidateProfile profile = new CandidateProfile();
        profile.setId(id);
        profile.setPosition("Java dev");
        profile.setCategory(Category.JAVA);
        profile.setOwner(user);

        when(service.readById(id)).thenReturn(profile);
        controller.delete(ownerId, id, authentication);

        verify(service, times(1)).delete(id);
    }
}
