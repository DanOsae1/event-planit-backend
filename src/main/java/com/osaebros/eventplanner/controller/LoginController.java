package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.exception.AuthException;
import com.osaebros.eventplanner.model.Credentials;
import com.osaebros.eventplanner.model.LoginResponse;
import com.osaebros.eventplanner.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserManagementService loginService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody Credentials credentials) throws AuthException {
        long start = System.currentTimeMillis();
        LoginResponse loginResponse = loginService.login(credentials);
        log.info("Log in completed. {}ms", System.currentTimeMillis() - start);
        return ResponseEntity.ok()
                .body(loginResponse);
    }

}
