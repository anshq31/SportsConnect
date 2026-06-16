package com.ansh.sportsconnect.service;

import com.ansh.sportsconnect.dto.UserBlockDto;
import com.ansh.sportsconnect.model.blockEntities.UserBlock;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import com.ansh.sportsconnect.repository.UserBlockRepository;
import com.ansh.sportsconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBlockService {

    @Autowired
    private UserBlockRepository userBlockRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
    }

    @Transactional
    public UserBlockDto blockUser(Long blockedId) {
        User blocker = getAuthenticatedUser();

        if (blocker.getId().equals(blockedId)) {
            throw new IllegalArgumentException("Cannot block yourself");
        }

        if (!userRepository.existsById(blockedId)) {
            throw new RuntimeException("User not found with id: " + blockedId);
        }

        if (userBlockRepository.existsByBlockerIdAndBlockedId(blocker.getId(), blockedId)) {
            throw new IllegalStateException("User is already blocked");
        }

        UserBlock block = UserBlock.builder()
                .blockerId(blocker.getId())
                .blockedId(blockedId)
                .build();
        UserBlock saved = userBlockRepository.save(block);

        return mapToDto(saved);
    }

    @Transactional
    public void unblockUser(Long blockedId) {
        User blocker = getAuthenticatedUser();
        userBlockRepository.deleteByBlockerIdAndBlockedId(blocker.getId(), blockedId);
    }

    private UserBlockDto mapToDto(UserBlock block) {
        return UserBlockDto.builder()
                .id(block.getId())
                .blockerId(block.getBlockerId())
                .blockedId(block.getBlockedId())
                .createdAt(block.getCreatedAt())
                .build();
    }
}
