package com.techdevweb.techdevbackend.Admin.Controller;

import com.techdevweb.techdevbackend.Admin.DTO.AdminCreateUserRequest;
import com.techdevweb.techdevbackend.Admin.DTO.AdminUpdateUserRequest;
import com.techdevweb.techdevbackend.Admin.Service.AdminUserService;
import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public List<User> getAllUsers() {
        User admin = currentUserResolver.getCurrentUser();
        return adminUserService.getAllUsers(admin);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        User admin = currentUserResolver.getCurrentUser();
        return adminUserService.getUserById(id, admin);
    }

    @PostMapping
    public User createUser(@RequestBody AdminCreateUserRequest request) {
        User admin = currentUserResolver.getCurrentUser();
        return adminUserService.createUser(request, admin);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequest request) {
        User admin = currentUserResolver.getCurrentUser();
        return adminUserService.updateUser(id, request, admin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User admin = currentUserResolver.getCurrentUser();
        adminUserService.deleteUser(id, admin);
        return ResponseEntity.noContent().build();
    }
}
