package com.osaebros.eventplanner.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.osaebros.eventplanner.model.Offering;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Table
@Entity
@SuperBuilder
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "bookings")
public class ServiceProvider extends UserAccount {


    private String profilePicture;

    private String username;

    private String bio;

    @ElementCollection
    private List<Link> links;

    private Offering offering;

    private List<String> tags;

    private String nylasExchangeCode;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @JsonManagedReference
    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToOne
    private ExternalCalender externalCalender;

    private BigDecimal hourlyRate;




}
