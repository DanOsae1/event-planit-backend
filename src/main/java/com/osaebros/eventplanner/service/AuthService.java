package com.osaebros.eventplanner.service;

import com.osaebros.eventplanner.exception.AuthException;
import com.osaebros.eventplanner.model.Credentials;
import com.osaebros.eventplanner.model.LoginResponse;
import com.osaebros.eventplanner.model.RegisterRequest;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    String createUser(RegisterRequest registerRequest) throws AuthException;

    UserRepresentation getUser(Credentials credentials);

    void updateUser(UserRepresentation userRepresentation);

    Boolean doesUserExists(Credentials credentials);

    void sendVerificationEmail(String userId, String redirecturl);

    void sendResetPassword(Credentials credentials);

    ResponseEntity<LoginResponse> login(Credentials credentials);
}
