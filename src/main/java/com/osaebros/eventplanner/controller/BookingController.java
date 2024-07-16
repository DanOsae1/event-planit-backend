package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.entity.Booking;
import com.osaebros.eventplanner.exception.BookingRecordException;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.BookingListEntry;
import com.osaebros.eventplanner.model.BookingRequest;
import com.osaebros.eventplanner.model.BookingResponse;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.service.BookingService;
import com.osaebros.eventplanner.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;

/**
 * To make sure that create booking is idempotent is to generate a idempotent key upon create reservation request.
 * Then create a PUT endpoint to confirm the created booking which should contain the idempotent key.
 * Then forward user to page to pay for the booking.
 * User will be forward to a booking dashboard page to manage the booking.
 * Option to make ammendments to time and date
 * Option to pay in full
 * This will contain message feature
 */
@RestController
@RequestMapping("/v1/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/presets")
    public ResponseEntity<Void> getBookingPresets() {
        log.info("Returning booking supported types");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable String bookingId) throws BookingRecordException {
        long start = System.currentTimeMillis();
        BookingDto bookingDto = bookingService.getBookingDto(bookingId);
        log.info("Retrieved booking. Time elapsed {} ms", System.currentTimeMillis() - start);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingDto);
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBookingEnquiry(@RequestHeader("Idempotent-Key") String idempotencyKey,
                                                           @Valid @RequestBody BookingRequest bookingRequest) throws ServiceProviderNotFoundException, BookingRecordException {
        long start = System.currentTimeMillis();
        BookingDto bookingDto = bookingService.createBooking(bookingRequest);
        log.info("Created a booking. time elapsed {} ms", System.currentTimeMillis() - start);
        return ResponseEntity
                .status(HttpStatusCode.valueOf(201))
                .body(bookingDto);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDto> confirmBookingEnquriy(@RequestHeader("Idempotent-Key") String idempotencyKey,
                                                            @PathVariable String bookingId,
                                                            @Valid @RequestBody BookingRequest bookingRequest) throws Exception {
        long start = System.currentTimeMillis();
        BookingDto bookingDto = bookingService.confirmBooking(bookingRequest);
        log.info("Confirmed booking. time elapsed {} ms", System.currentTimeMillis() - start);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("clientSecret", bookingDto.getPaymentSession())
                .body(bookingDto);
    }
//
//    @PutMapping
//    public ResponseEntity<BookingDto> updateBookingEnquiry(@RequestHeader("Idempotent-Key") String idempotencyKey,
//                                                           @RequestParam("${bookingId}") String bookingId,
//                                                           @Valid @RequestBody BookingRequest bookingRequest) throws ServiceProviderNotFoundException, BookingRecordException {
//        long start = System.currentTimeMillis();
//        BookingDto bookingDto = bookingService.updateBooking(bookingId, bookingRequest);
//        log.info("Updated booking: {}. time elapsed {} ms", bookingDto.getBookingId(),System.currentTimeMillis() - start);
//
//        return ResponseEntity
//                .status(HttpStatusCode.valueOf(204))
//                .body(bookingDto);
//    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<BookingListEntry>> getUpcomingBookings(@RequestHeader("User-Id") String userId) {
        long start = System.currentTimeMillis();
        log.info("Retrieving future bookings for {}", userId);
        List<BookingListEntry> upcomingBookings = bookingService.getUpcomingBookings(userId);
        log.info("Request completed. {}ms", System.currentTimeMillis() - start);
        return ResponseEntity.ok(upcomingBookings);
    }

    @GetMapping("/history/{startDate}/{endDate}")
    public ResponseEntity<?> getHistoryBookings(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("User-Id") String userId) {

        log.info("Retrieving historical bookings for user {} between {} and {}", userId, startDate, endDate);

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body("Start date must be before or equal to end date");
        }

        try {
            long start = System.currentTimeMillis();
            List<BookingListEntry> historicalBookings = bookingService.getHistoricalBookings(userId, startDate, endDate);
            long duration = System.currentTimeMillis() - start;

            log.info("Retrieved {} historical bookings for user {}. Request completed in {}ms",
                    historicalBookings.size(), userId, duration);

            return ResponseEntity.ok(historicalBookings);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for historical bookings request", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving historical bookings for user {}", userId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }


}
