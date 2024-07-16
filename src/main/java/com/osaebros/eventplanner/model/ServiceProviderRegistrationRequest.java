package com.osaebros.eventplanner.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ServiceProviderRegistrationRequest {
    Location homeLocation;
    String offering;
    String bio;
    List<String> tags;
    Map<String, String> links;
    Map<String, String> images;
    String requirements;
    BigDecimal hourlyRate;
    Boolean agreeToNylas;
    String emailAddress;
}
