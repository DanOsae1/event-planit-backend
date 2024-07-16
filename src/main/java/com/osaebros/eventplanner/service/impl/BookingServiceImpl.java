package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.Booking;
import com.osaebros.eventplanner.entity.Location;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.exception.BookingRecordException;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.BookingListEntry;
import com.osaebros.eventplanner.model.BookingRequest;
import com.osaebros.eventplanner.model.BookingStatus;
import com.osaebros.eventplanner.repository.BookingRepository;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.repository.mapping.BookingMapper;
import com.osaebros.eventplanner.service.BookingService;
import com.osaebros.eventplanner.service.PaymentService;
import com.osaebros.eventplanner.service.ServiceProviderService;
import com.osaebros.eventplanner.utils.GeometryUtils;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceProviderService serviceProviderService;
    private final BookingMapper bookingMapper;
    private final GeometryUtils geometryFactory;
    private final PaymentService paymentService;

    @Override
    public BookingDto getBookingDto(String bookingId) throws BookingRecordException {
        Booking booking = bookingServiceRepository()
                .findByBookingId(bookingId)
                .orElseThrow(() -> new BookingRecordException("Could not find booking with given id"));
        return bookingMapper.bookingToDto(booking);
    }


    @Override
    public BookingRepository bookingServiceRepository() {
        return bookingRepository;
    }

    //    TODO: Make this method idempotent
    @Override
    @Transactional
    public BookingDto createBooking(BookingRequest bookingRequest) throws BookingRecordException, ServiceProviderNotFoundException {
        //TODO:- create ideompotency logic service before saving booking enquiry
        assert bookingRequest.getUserAccountRef() != null;
        assert bookingRequest.getUserid() != null;

        //Find the user willing to book
        String userId = bookingRequest.getUserid();
        log.info("Looking for User {}", userId);
//        GeneralCustomerAccount customerAccount = ...
//        String customerAccountEmail =

        //Find the service provider
        String userAccountRef = bookingRequest.getUserAccountRef();
        log.info("Looking for service provider {}", userAccountRef);
        ServiceProvider serviceProvider = serviceProviderService.getServiceProvider(userAccountRef);
        log.debug("Found service provider {}", serviceProvider.getUsername());

        //Check if service provider can fulfill event
        LocalDateTime startTime = bookingRequest.getStartDate();
        LocalDateTime endTime = bookingRequest.getEndDate();

        log.info("Checking availability for proposed event");
        Boolean canAcceptBooking = serviceProviderService.canAcceptBooking(serviceProvider, startTime, endTime);

        if (!canAcceptBooking) {
            throw new BookingRecordException("Service provider unavailable for selected times");
        }

        log.info("Availability confirmed, creating booking... ");
        long startProcess = System.currentTimeMillis();

        String address = bookingRequest.getLocation().getAddress();
        String postcode = bookingRequest.getLocation().getPostcode();
        Double lat = bookingRequest.getLocation().getLat();
        Double lon = bookingRequest.getLocation().getLon();
        String bookingNotes = bookingRequest.getBookingNotes();

//        TODO:- Implement cost calculator
//        BigDecimal cost = serviceProviderService.calculateEventCharge(serviceProvider.getServiceProviderId(),numberOfAttendees,location,startDate,endDate);

        Booking createdBooking = Booking.builder()
                .bookingDate(startTime)
                .bookingEndDate(endTime)
                .bookingStatus(BookingStatus.ENQUIRED)
                .provider(serviceProvider)
//                .bookingEmail(customerAccountEmail)
                .location(Location.builder().postcode(postcode).address(address).point(geometryFactory.createPoint(lon, lat)).build())
//                .notes(bookingNotes)
                .numberOfAttendees(bookingRequest.getNumberOfAttendees())
                .cost(calculateEventCost(bookingRequest.getNumberOfAttendees(), serviceProvider.getHourlyRate()))
                .build();

        //save to redis - idempotent
        Booking booking = bookingRepository.save(createdBooking);

//        Booking bookingWithPayment = setPaymentReferenceForBooking(booking);

        BookingDto bookingDto = bookingMapper.bookingToDto(booking);

        log.info("Created booking in DB, {}ms", System.currentTimeMillis() - startProcess);
        //return booking dto
        return bookingDto;
    }

    /**
     * @param bookingRequest
     * @return
     * @throws BookingRecordException This method is called after create booking. This is not a full update method, only use to confirm payment
     *                                and AI input in notes (user inspiration)
     */

    @Override
    public BookingDto confirmBooking(BookingRequest bookingRequest) throws BookingRecordException, StripeException {
        long start = System.currentTimeMillis();
        log.info("Looking for booking {}", bookingRequest.getBookingId());
        Booking booking = getBooking(bookingRequest.getBookingId());

        log.info("Confirm booking");
        String notes = bookingRequest.getBookingNotes();
        String stripePaymentSession = paymentService.getStripePaymentSession(booking);
        booking.setNotes(notes);
        booking.setStripePaymentReference(stripePaymentSession);
        booking.setPaymentReference(stripePaymentSession);
        booking.setBookingStatus(BookingStatus.UNPAID);

        log.info("Saving");
        Booking confirmed = bookingRepository.save(booking);
        BookingDto bookingDto = bookingMapper.bookingToDto(confirmed);

        log.info("Completed. {}ms", System.currentTimeMillis() - start);
        return bookingDto;
    }


    private BigDecimal calculateEventCost(Long numberOfAttendees, BigDecimal hourlyRate) {
        return BigDecimal.valueOf(100L);
    }


    @Override
    public void UpdateBooking() {

    }

    @Override
    public List<Booking> getBookings() {
        return null;
    }


    @Override
    public List<BookingListEntry> getUpcomingBookings(String userId) {
        LocalDateTime today = LocalDateTime.now();
        log.info("Searching db for upcoming events {} for {}", today, userId);
        List<Booking> bookings = bookingRepository.findUpcomingBookingsForProvider(userId, today);
        List<BookingListEntry> upcoming = bookings.stream()
                .map(bookingMapper::bookingToBookingListEntry)
                .collect(Collectors.toList());
        log.info("Found {} entries", upcoming.size());
        return upcoming;
    }

    @Override
    public List<BookingListEntry> getHistoricalBookings(String userId, LocalDate startDate, LocalDate endDate) {

        return null;
    }

    //    TODO:- refactor to edit booking
    @Override
    public BookingDto updateBooking(String bookingId, BookingRequest bookingRequest) {
        return null;
    }


}
