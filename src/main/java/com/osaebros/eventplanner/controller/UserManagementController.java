package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.model.UpdateUserProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserManagementController {

    @PutMapping
    public ResponseEntity<Void> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest userProfileRequest) {
        return ResponseEntity.ok().build();

    }
}
