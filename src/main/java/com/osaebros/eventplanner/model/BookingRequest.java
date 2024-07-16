package com.osaebros.eventplanner.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.osaebros.eventplanner.utils.CustomLocalDateTimeDeserializer;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {

    String date;
    String time;
    Integer duration;
    Location location;
    @Email
    String bookingEmail;
    String userAccountRef;
    String bookingId;
    String bookingNotes;
    Long numberOfAttendees;
    String userid;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    LocalDateTime startDate;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    LocalDateTime endDate;

}
