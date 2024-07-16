package com.osaebros.eventplanner.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osaebros.eventplanner.exception.AuthException;
import com.osaebros.eventplanner.model.Credentials;
import com.osaebros.eventplanner.model.LoginResponse;
import com.osaebros.eventplanner.model.RegisterRequest;
import com.osaebros.eventplanner.service.AuthService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakAuthServiceImpl implements AuthService {

    private final Keycloak keycloak;
    private final RestClient restClient;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String keyCloakClientId;

    @Value("${keycloak.client.client-secret}")
    private String keyCloakClientSecret;

    @Value("keycloak.auth-server-url")
    private String KEYCLOACK_AUTH_URL;

    @Value("application.frontend_url")
    private String FRONTEND_REDIRECT_URI;

    private UsersResource getUserResource() {
        return keycloak.realm(keycloakRealm).users();
    }

    @Override
    public String createUser(RegisterRequest registerRequest) throws AuthException {
        Map<String, List<String>> clientRoles = new HashMap<>();
        Map<String, List<String>> attributes = new HashMap<>();
        Credentials credentials = new Credentials(registerRequest.getEmail(), registerRequest.getPassword());
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(credentials.getPassword());
        List<String> realmRoles = new ArrayList<>();

        realmRoles.add("USER");
        UserRepresentation user = new UserRepresentation();
        user.setEmail(credentials.getEmailAddress());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setUsername(credentials.getEmailAddress());
        user.setCredentials(Collections.singletonList(credentialRepresentation));
        user.setEnabled(true);
        user.setRequiredActions(List.of("verify_email"));

        if (registerRequest.getIsServiceProvider()) {
            realmRoles.add("SERVICE_PROVIDER");
            List<String> accountTypeList = new ArrayList<>();
            accountTypeList.add(registerRequest.getIsServiceProvider() ? "service_provider" : "normal_user");
            attributes.put("accountType", accountTypeList);
        }
        user.setClientRoles(clientRoles);
        user.setRealmRoles(realmRoles);
        user.setAttributes(attributes);

        String createduserId = createUser(user);

        return createduserId;
    }

    @Override
    public void sendVerificationEmail(String userId, String redirectUrl) {

        getUserResource().get(userId)
                .sendVerifyEmail(keyCloakClientId, Objects.nonNull(redirectUrl) ? redirectUrl : FRONTEND_REDIRECT_URI);
    }

    private UserRepresentation getUser(String userId) {
        return getUserResource().get(userId).toRepresentation();
    }

    @Override
    public UserRepresentation getUser(Credentials credentials) {
        return getUserResource()
                .searchByEmail(credentials.getEmailAddress(), true)
                .stream()
                .filter(ur -> ur.getEmail().equalsIgnoreCase(credentials.getEmailAddress()))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void updateUser(UserRepresentation userRepresentation) {
        getUserResource().get(userRepresentation.getId()).update(userRepresentation);
    }

    private String createUser(UserRepresentation user) throws AuthException {
        assert !user.getEmail().isEmpty();
        assert !user.getCredentials().isEmpty();

        try (Response response = getUserResource().create(user)) {
            int status = response.getStatus();
            log.info("Response Status: {} Info: {}", status, response.getStatusInfo());

            if (status != Response.Status.CREATED.getStatusCode()) {
                String errorMsg = String.format("Failed to create user. Status: %d Info: %s", status, response.getStatusInfo());
                log.error(errorMsg);
                throw new AuthException(errorMsg);
            }

            return CreatedResponseUtil.getCreatedId(response);

        } catch (Exception e) {
            log.error("Exception occurred while creating user", e);
            throw new AuthException("Exception occurred while creating user" + e.getMessage());
        }

    }

    @Override //Needs better exception handling
    public Boolean doesUserExists(Credentials credentials) {
        UsersResource userResource = getUserResource();
        List<UserRepresentation> user = userResource.searchByEmail(credentials.getEmailAddress(), true);
        return !user.isEmpty();
    }

    @Override
    public void sendResetPassword(Credentials credentials) {
        UsersResource usersResource = getUserResource();
        UserRepresentation userRepresentation = getUser(credentials);
        usersResource.get(userRepresentation.getId()).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
    }

    @Override
    public ResponseEntity<LoginResponse> login(Credentials credentials) {
        log.info("User logging in");
        String email = credentials.getEmailAddress();
        String password = credentials.getPassword();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", OAuth2Constants.PASSWORD);
        body.add("client_id", keyCloakClientId);
        body.add("client_secret", keyCloakClientSecret);
        body.add("username", email);
        body.add("password", password);
        return authenticateWithKeyCloak(body);
    }


    public ResponseEntity<LoginResponse> refresh(String token) {
        log.info("Fetching refresh token");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", OAuth2Constants.REFRESH_TOKEN);
        body.add("client_id", keyCloakClientId);
        body.add("client_secret", keyCloakClientSecret);
        body.add("refresh_token", token);
        return authenticateWithKeyCloak(body);
    }

    private ResponseEntity<LoginResponse> authenticateWithKeyCloak(MultiValueMap<String, String> body) {
        return restClient.post()
                .uri(KEYCLOACK_AUTH_URL + "realms/event-planit-realm/protocol/openid-connect/token")
                .headers(httpHeaders -> httpHeaders.add("Content-Type", "application/x-www-form-urlencoded"))
                .body(body)
                .retrieve()
                .toEntity(LoginResponse.class);
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }

}
