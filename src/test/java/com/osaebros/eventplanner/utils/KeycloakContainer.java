package com.osaebros.eventplanner.utils;

import lombok.Getter;
import org.testcontainers.containers.GenericContainer;

@Getter
public class KeycloakContainer extends GenericContainer<KeycloakContainer> {
    private static final int KEYCLOAK_PORT = 18993;
    String user = "admin";
    String pass = "admin";
    String masterRealm = "master";
    String clientId = "clientid";

    public KeycloakContainer() {
        super("quay.io/keycloak/keycloak:24.0.5");
        addExposedPort(KEYCLOAK_PORT);
        addEnv("KEYCLOAK_USER", user);
        addEnv("KEYCLOAK_PASSWORD", pass);
    }

    public String getAuthServerUrl() {
        return String.format("http://%s:%s/auth", getHost(), getMappedPort(KEYCLOAK_PORT));
    }


}