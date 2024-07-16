package com.osaebros.eventplanner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListEntry {

    private String bookingId;
    private String eventName;
    private Long cost;
    private String bookingDate;
    private String serviceProviderProfileImage;
    private BookingStatus bookingStatus;
}
