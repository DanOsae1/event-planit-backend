package com.osaebros.eventplanner.utils;

import com.osaebros.eventplanner.entity.ServiceProvider;

public class TestUtils {

    public static ServiceProvider getTestServiceProvider(String username) {
        return ServiceProvider.builder()
                .userAccountRef("photoman1")
                .username(username)
                .build();
    }

    public static ServiceProvider getTestServiceProvider() {
        return ServiceProvider.builder().build();
    }
}
