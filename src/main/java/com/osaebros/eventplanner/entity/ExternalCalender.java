package com.osaebros.eventplanner.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalCalender {

    @Id
    public Long id;

    private String name;

    private String grantTypes;

//    @OneToOne
//    private ServiceProvider serviceProvider;

    private String nylasCalenderId;

    private String nylasGrantId;
}
