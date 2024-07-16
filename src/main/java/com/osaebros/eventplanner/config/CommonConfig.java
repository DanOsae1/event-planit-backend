package com.osaebros.eventplanner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;

@Configuration
public class CommonConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }



    @Bean
    public DateTimeFormatter formatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm");
    }

    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory(new PrecisionModel(), 4326);
    }


}
