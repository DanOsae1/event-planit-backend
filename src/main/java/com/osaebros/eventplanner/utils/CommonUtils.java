package com.osaebros.eventplanner.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CommonUtils {

    public static LocalDateTime getLocalDateTimeFromRequest(String date, String time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String dateString = date + "T"+time;
        return LocalDateTime.parse(dateString, dateTimeFormatter);
    }
}
