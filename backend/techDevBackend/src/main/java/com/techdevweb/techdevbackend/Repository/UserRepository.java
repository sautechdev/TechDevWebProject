package com.techdevweb.techdevbackend.Repository;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Enum.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // AdminSeeder'in "hic admin var mi" kontrolu icin
    boolean existsByRole(UserRole role);
}
