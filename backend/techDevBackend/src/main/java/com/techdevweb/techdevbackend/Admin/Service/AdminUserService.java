package com.techdevweb.techdevbackend.Admin.Service;

import com.techdevweb.techdevbackend.Admin.DTO.AdminCreateUserRequest;
import com.techdevweb.techdevbackend.Admin.DTO.AdminUpdateUserRequest;
import com.techdevweb.techdevbackend.Entity.User;

import java.util.List;

public interface AdminUserService {
    List<User> getAllUsers(User admin);
    User getUserById(Long id, User admin);
    User createUser(AdminCreateUserRequest request, User admin);
    User updateUser(Long id, AdminUpdateUserRequest request, User admin);
    void deleteUser(Long id, User admin);
}
