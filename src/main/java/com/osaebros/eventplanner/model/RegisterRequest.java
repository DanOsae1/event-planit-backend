package com.osaebros.eventplanner.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegisterRequest {

    @Email
    private String email;
    private String password;
    private String phoneNumber;
    private Boolean isServiceProvider = false;

    //personal details;
//    private String displayName;
    private String firstName;
    private String lastName;
    private String username;
    private String bio;

    private Location location;


//    private String firstLineAdd;
//    private String secondLineAdd;
//    private String city;
//    private String postcode;
//    private Double latitude;
//    private Double longitude;

    //Offering
//    private String offering;
//    private List<String> tags;

    //Apis permissions
//    private String nylasCalenderId;
//    private String nylasGrantId;

    //Social medial links
//    private String instagram;
//    private String website;
}
