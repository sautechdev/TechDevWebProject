package com.techdevweb.techdevbackend.Profile.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Profile.DTO.ProfileResponse;
import com.techdevweb.techdevbackend.Profile.DTO.UpdateProfileRequest;

public interface ProfileService {
    ProfileResponse getProfile(User user);
    ProfileResponse updateProfile(User user, UpdateProfileRequest request);
}
