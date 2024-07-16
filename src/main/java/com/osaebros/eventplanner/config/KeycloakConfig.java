package com.osaebros.eventplanner.config;


import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.keycloak.admin.client.Keycloak;

import java.util.Objects;

@Configuration
@Slf4j
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.master.realm}")
    private String keyClockMasterRealm;

    @Value("${keycloak.client_id}")
    private String keycloakClientId;


    @Value("${keycloak-admin.username}")
    private String keycloakAdminUsername;

    @Value("${keycloak-admin.password}")
    private String keycloakAdminPassword;


    @Bean
    public Keycloak keyCloak() {

        return KeycloakBuilder.builder()
                .realm(keyClockMasterRealm)
                .serverUrl(keycloakServerUrl)
                .clientId(keycloakClientId)
                .username(keycloakAdminUsername)
                .password(keycloakAdminPassword)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}
