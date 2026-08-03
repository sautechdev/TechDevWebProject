package com.techdevweb.techdevbackend.Profile.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Profile.DTO.ProfileResponse;
import com.techdevweb.techdevbackend.Profile.DTO.UpdateProfileRequest;
import com.techdevweb.techdevbackend.Profile.Service.ProfileService;
import com.techdevweb.techdevbackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ProfileResponse getProfile(User user) {
        // Lazy skills koleksiyonunu guvenli sekilde yuklemek icin yonetilen entity'yi cekiyoruz
        User managedUser = userRepository.findById(user.getId()).orElse(user);
        return toResponse(managedUser);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(User user, UpdateProfileRequest request) {
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getCurrentPassword() == null
                    || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new AccessDeniedException("Mevcut sifre hatali");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    private ProfileResponse toResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt(),
                new ArrayList<>(user.getSkills()));
    }
}
