package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.exception.AuthException;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.exception.UserAccountExistsException;
import com.osaebros.eventplanner.model.RegisterRequest;
import com.osaebros.eventplanner.model.RegisterResponse;
import com.osaebros.eventplanner.model.ServiceProviderRegistrationRequest;
import com.osaebros.eventplanner.repository.dto.ServiceProviderDto;
import com.osaebros.eventplanner.repository.dto.UserAccountDto;
import com.osaebros.eventplanner.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.jose.jwk.JWK;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("v1/registration")
@Slf4j
@RequiredArgsConstructor
public class RegistrationController {

    private final UserManagementService service;

    @PostMapping("/create-account")
    public ResponseEntity<UserAccountDto> createUserAccount(
            @Valid @RequestBody RegisterRequest registrationRequest) throws UserAccountExistsException, AuthException {
        UserAccountDto userAccountDto = service.createAccount(registrationRequest);
        return ResponseEntity
                .status(CREATED)
                .body(userAccountDto);
    }

    @GetMapping("/resend-email-verification")
    public ResponseEntity<Void> sendEmailVerification(@PathVariable("email") String email) throws UserAccountExistsException {
        log.info("Sending email to {}", email);
        service.sendEmailVerification(email);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/confirmRegistration/{email}")
    public ResponseEntity<UserAccountDto> confirmServiceProviderAccount(@PathVariable("email") String email) throws ServiceProviderNotFoundException {
        long start = System.currentTimeMillis();
        log.info("Confirming account registration");
        UserAccountDto userAccountDto = service.getRegistrationAccount(email);
        log.info("Retrieved account for {}. Time taken {}ms", email, System.currentTimeMillis() - start);
        return ResponseEntity
                .ok().body(userAccountDto);

    }

    @PutMapping("/create-account-service-provider-details")
    public ResponseEntity createFullAccountForServiceProvider(
            @Valid @RequestBody ServiceProviderRegistrationRequest registrationRequest) throws ServiceProviderNotFoundException {
        RegisterResponse response = service.createServiceProviderAccount(registrationRequest);
//        redirect to dashboard
        return ResponseEntity
                .status(HttpStatusCode.valueOf(201))
                .body(response);
    }

    @GetMapping("/external-calendar-authentication")
    public ResponseEntity<String> confirmAccountForRegistration(@RequestParam String email) {
        log.info("Verifying account");
        String response = service.confirmAccountForNylasIntegration(email);
        return ResponseEntity.ok(response);
    }
}
