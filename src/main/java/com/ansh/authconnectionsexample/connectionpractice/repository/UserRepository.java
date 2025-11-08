package com.ansh.authconnectionsexample.connectionpractice.repository;

import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
