package com.osaebros.eventplanner.repository.dto;

import com.osaebros.eventplanner.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDto {

    String bookingId;
    String userEmail;
    String userRef;
    String serviceProviderRef;
    String serviceProviderEmail;
    Location location;
    String notes;
    String date;
    String endTime;
    String status;
    List<String> history;
    BigDecimal totalServiceCost;
    String paymentSession;
}
