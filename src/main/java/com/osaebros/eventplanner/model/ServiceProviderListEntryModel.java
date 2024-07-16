package com.osaebros.eventplanner.model;

import lombok.Data;

@Data
public class ServiceProviderListEntryModel {

    private String userAccountRef;

    private String username;

    private String profilePicture;

    private double distance;

    private String offering;

    private Boolean available;

}
