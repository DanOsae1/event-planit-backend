package com.osaebros.eventplanner.model;

import java.util.Arrays;

public enum BookingStatus {

    ENQUIRED, PAID, UNPAID, COMPLETED, OPEN, CANCELLED;

    public static BookingStatus getBookingStatus(String status){
        return Arrays.stream(values()).filter(bs -> bs.name().equalsIgnoreCase(status))
                .findFirst()
                .orElse(OPEN);
    }
}
