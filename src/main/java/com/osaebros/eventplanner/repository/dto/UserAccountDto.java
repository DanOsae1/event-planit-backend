package com.osaebros.eventplanner.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAccountDto {

    String userAccountRef;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
}
