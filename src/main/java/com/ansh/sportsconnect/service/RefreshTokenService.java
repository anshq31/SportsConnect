package com.ansh.sportsconnect.service;

import com.ansh.sportsconnect.model.userAndAuthEntities.RefreshToken;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import com.ansh.sportsconnect.repository.RefreshTokenRepository;
import com.ansh.sportsconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.refresh.expiration-ms}")
    private Long refreshTokenDurationMs;

    @Transactional
    public RefreshToken createRefreshToken(User user){

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        RefreshToken tokenToSave;

        if (existingTokenOpt.isPresent()){
            tokenToSave = existingTokenOpt.get();
            if (!isExpired(tokenToSave)){
                return tokenToSave;
            }
            tokenToSave.setToken(UUID.randomUUID().toString());
            tokenToSave.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        }else {
            tokenToSave = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                    .build();
        }

        return refreshTokenRepository.save(tokenToSave);
    }

    public Optional<RefreshToken> findByToken(String token){
        return  refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken token){
        return token.getExpiryDate().isBefore(Instant.now());
    }

}
