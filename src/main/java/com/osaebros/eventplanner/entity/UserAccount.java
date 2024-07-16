package com.osaebros.eventplanner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@ToString
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userAccountRef;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    @Column(unique = true)
    private String KeycloakId;

    @PrePersist
    public void prePersist() {
        if (userAccountRef == null) {
            userAccountRef = UUID.randomUUID().toString();
        }
    }

}
