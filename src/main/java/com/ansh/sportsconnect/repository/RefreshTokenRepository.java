package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.userAndAuthEntities.RefreshToken;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
//    void deleteByUser(User user);
}
