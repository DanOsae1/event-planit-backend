package com.osaebros.eventplanner.config;

import com.nylas.NylasClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NylasConfig {

    @Value("${nylas.api_key}")
    private String API_KEY;

    @Bean
    public NylasClient nylasClient() {
        return new NylasClient.Builder(API_KEY).build();
    }
}
