package com.osaebros.eventplanner.utils;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:latest";
    private static final String DATABASE_NAME = "event-planit";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    private static PostgresContainer container;

    private PostgresContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgresContainer getInstance() {
        if (container == null) {
            container = new PostgresContainer()
                    .withDatabaseName(DATABASE_NAME)
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    public String getJdbcUrl() {
        return String.format("jdbc:postgresql://localhost:%d/%s", getMappedPort(POSTGRESQL_PORT), DATABASE_NAME);
    }
}