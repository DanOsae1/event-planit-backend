package com.osaebros.eventplanner.service;

import com.osaebros.eventplanner.entity.Booking;
import com.osaebros.eventplanner.exception.BookingRecordException;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.BookingListEntry;
import com.osaebros.eventplanner.model.BookingRequest;
import com.osaebros.eventplanner.model.BookingResponse;
import com.osaebros.eventplanner.repository.BookingRepository;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.repository.mapping.BookingMapper;
import com.stripe.exception.StripeException;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    default Booking getBooking(String bookingId) throws BookingRecordException {
        return bookingServiceRepository().findByBookingId(bookingId).orElseThrow(() -> new BookingRecordException("Could not find booking with given id"));
    }

    BookingDto getBookingDto(String bookingId) throws BookingRecordException;

    BookingRepository bookingServiceRepository();

    BookingDto createBooking(BookingRequest bookingRequest) throws ServiceProviderNotFoundException, BookingRecordException;

    BookingDto confirmBooking(BookingRequest bookingRequest) throws ServiceProviderNotFoundException, BookingRecordException, StripeException;

    void UpdateBooking();

    List<Booking> getBookings();


    List<BookingListEntry> getUpcomingBookings(String userId);

    List<BookingListEntry> getHistoricalBookings(String userId, LocalDate startDate, LocalDate endDate);

    BookingDto updateBooking(String bookingId, BookingRequest bookingRequest);

}
