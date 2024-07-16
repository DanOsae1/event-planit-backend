package com.osaebros.eventplanner.model;

import lombok.Data;

import java.util.List;

@Data
public class RegistrationFormAutoCompleteResponse {

    private List<String> offeringOptions;
    private List<String> tagsOptions;

}
