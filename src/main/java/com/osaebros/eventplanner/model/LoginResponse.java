package com.osaebros.eventplanner.model;

import lombok.Data;

@Data
public class LoginResponse {

    String accessToken;
    String refreshToken;
}
