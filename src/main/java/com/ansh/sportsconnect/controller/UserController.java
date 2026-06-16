package com.ansh.sportsconnect.controller;

import com.ansh.sportsconnect.dto.UserBlockDto;
import com.ansh.sportsconnect.dto.UserProfileDto;
import com.ansh.sportsconnect.dto.UserUpdateDto;
import com.ansh.sportsconnect.service.UserBlockService;
import com.ansh.sportsconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserBlockService userBlockService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile() {
        UserProfileDto profile = userService.getMyProfile();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateMyProfile(@Valid @RequestBody UserUpdateDto updateDto) {
        UserProfileDto updatedProfile = userService.updateMyProfile(updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            UserProfileDto profile = userService.getUserPublicProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long userId) {
        try {
            UserBlockDto block = userBlockService.blockUser(userId);
            return new ResponseEntity<>(block, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/block")
    public ResponseEntity<Void> unblockUser(@PathVariable Long userId) {
        userBlockService.unblockUser(userId);
        return ResponseEntity.noContent().build();
    }
}
