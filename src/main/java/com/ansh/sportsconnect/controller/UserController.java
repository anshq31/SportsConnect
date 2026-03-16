package com.ansh.sportsconnect.controller;

import com.ansh.sportsconnect.dto.UserProfileDto;
import com.ansh.sportsconnect.dto.UserUpdateDto;
import com.ansh.sportsconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private  UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile(){
        UserProfileDto profile = userService.getMyProfile();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateMyProfile(@Valid @RequestBody UserUpdateDto updateDto){
        UserProfileDto updatedProfile = userService.updateMyProfile(updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId){
        UserProfileDto profile = userService.getUserPublicProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
