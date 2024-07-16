package com.osaebros.eventplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProviderProfileModel {

    String userAccountRef;
    String username;
    String bio;
    Map<String, String> links;
    String profilePicture;
    Offering offering;
    List<String> tags;
}
