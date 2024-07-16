package com.osaebros.eventplanner.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.osaebros.eventplanner.model.BookingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Table
@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "provider")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bookingId;

    private String eventName;

    private LocalDateTime bookingDate;

    private LocalDateTime bookingEndDate;

    BookingStatus bookingStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    ServiceProvider provider;

    @OneToOne(cascade = CascadeType.ALL)
    private Location location;

    String userEmail;

    String bookingUserId;

    String notes;

    Long numberOfAttendees;

    String paymentReference;

    BigDecimal cost;

    String stripePaymentReference;

    @PrePersist
    public void createBookingId() {
        if (bookingId == null) {
            UUID uuid = UUID.randomUUID();
            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());
            bookingId = Base64.getUrlEncoder().withoutPadding().encodeToString(bb.array()).substring(0, 15);
        }
    }


}
