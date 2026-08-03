package com.techdevweb.techdevbackend.Admin.ServiceImpl;

import com.techdevweb.techdevbackend.Admin.DTO.AdminCreateUserRequest;
import com.techdevweb.techdevbackend.Admin.DTO.AdminUpdateUserRequest;
import com.techdevweb.techdevbackend.Admin.Service.AdminUserService;
import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Exception.ConflictException;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Repository.UserRepository;
import com.techdevweb.techdevbackend.Security.AdminAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminAccessGuard adminAccessGuard;

    @Override
    public List<User> getAllUsers(User admin) {
        adminAccessGuard.requireAdmin();
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id, User admin) {
        adminAccessGuard.requireAdmin();
        return findUserOrThrow(id);
    }

    @Override
    @Transactional
    public User createUser(AdminCreateUserRequest request, User admin) {
        adminAccessGuard.requireAdmin();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Bu e-posta adresi zaten kayitli");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() != null ? request.getRole() : com.techdevweb.techdevbackend.Enum.UserRole.USER);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, AdminUpdateUserRequest request, User admin) {
        adminAccessGuard.requireAdmin();
        User user = findUserOrThrow(id);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, User admin) {
        adminAccessGuard.requireAdmin();
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + id));
    }
}
