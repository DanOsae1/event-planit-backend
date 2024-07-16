package com.osaebros.eventplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Data
public class BookingResponse {

    String bookingId;
//    String userEmail;
//    String serviceProviderRef;
//    String serviceProviderEmail;
//    String location;
//    String notes;
//    String date;
//    String endTime;
//    String status;
//    List<String> history;
//    BigDecimal totalServiceCost;
    String paymentSession;


}
