package com.example.demo.service;

import com.example.demo.model.Profile;
import com.example.demo.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final AuthService authService;

    public ProfileService(ProfileRepository profileRepository, AuthService authService) {
        this.profileRepository = profileRepository;
        this.authService = authService;
    }

    @Transactional
    public Profile getProfileById(UUID id, UUID token) {
        if (authService.validateToken(token)) {
            return profileRepository.getProfileById(id);
        } else {
            return null;
        }
    }
}
