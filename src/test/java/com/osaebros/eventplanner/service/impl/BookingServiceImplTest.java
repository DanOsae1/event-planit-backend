package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.Booking;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.exception.BookingRecordException;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.BookingListEntry;
import com.osaebros.eventplanner.model.BookingRequest;
import com.osaebros.eventplanner.model.Location;
import com.osaebros.eventplanner.repository.BookingRepository;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.repository.mapping.BookingMapper;
import com.osaebros.eventplanner.utils.GeometryUtils;
import com.osaebros.eventplanner.utils.TestUtils;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    private static final String MOCK_STRIPE_SESSION = "STRIPE_SESSION";
    @InjectMocks
    BookingServiceImpl classUnderTest;

    @Mock
    BookingMapper mapper;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    GeometryUtils geometryFactory;

    @Mock
    CalendarServiceImpl calendarService;

    @Mock
    PaymentServiceImpl paymentService;

    @Mock
    ServiceProviderServiceImpl serviceProviderService;
    BookingRequest bookingRequest;
    String serviceProviderId = "photoman1";
    LocalDateTime startDate;
    LocalDateTime endDate;


    @BeforeEach
    public void setup() {
        //Given
        startDate = LocalDateTime.now();
        endDate = startDate.plus(1, ChronoUnit.HOURS);
        Integer duration = 1;
        Double lat = 0D;
        Double lon = 0D;
        String bookingEmail = "me@me.com"; //the person booking the event
        String bookingId = "uniqueId";
        Location location = Location.builder()
                .address("Aburi")
                .lon(lon)
                .lon(lat).build();
        Long numberOfAttendees = 10L;
        String bookingNotes = "Some Requirements";
        bookingRequest = BookingRequest
                .builder()
                .location(location)
                .bookingId(bookingId)
                .bookingEmail(bookingEmail)
                .duration(duration)
                .userAccountRef(serviceProviderId)
                .bookingNotes(bookingNotes)
                .numberOfAttendees(numberOfAttendees)
                .startDate(startDate)
                .userid(bookingEmail)
                .endDate(endDate)
                .build();
    }

    @Test
    public void handleCreateBookingFromIncomingParameters() throws ServiceProviderNotFoundException, BookingRecordException, StripeException {

        ServiceProvider mockServiceProvider = TestUtils.getTestServiceProvider("Test1");
        mockServiceProvider.setUserAccountRef(serviceProviderId);

        BookingDto bookingdto = new BookingDto();
        bookingdto.setPaymentSession(MOCK_STRIPE_SESSION);

        when(serviceProviderService.getServiceProvider(serviceProviderId)).thenReturn(mockServiceProvider);
        when(serviceProviderService.canAcceptBooking(mockServiceProvider, startDate, endDate)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(mock(Booking.class));
        when(mapper.bookingToDto(any(Booking.class))).thenReturn(bookingdto);

        //When
        BookingDto bookingResponse = classUnderTest.createBooking(bookingRequest);

        //then
        assertNotNull(bookingResponse);
        assertEquals(bookingResponse.getPaymentSession(), MOCK_STRIPE_SESSION);

        verify(bookingRepository).save(any(Booking.class));
        verify(serviceProviderService).getServiceProvider(serviceProviderId);
        verify(serviceProviderService).canAcceptBooking(mockServiceProvider, startDate, endDate);
        verify(mapper).bookingToDto(any(Booking.class));
    }

    @Test
    public void shouldNotBookEventIfThereIsACalendarClash() throws ServiceProviderNotFoundException, StripeException {
        // Given
        ServiceProvider mockServiceProvider = TestUtils.getTestServiceProvider("Test1");
        mockServiceProvider.setUserAccountRef(serviceProviderId);

        when(serviceProviderService.getServiceProvider(serviceProviderId)).thenReturn(mockServiceProvider);
        when(serviceProviderService.canAcceptBooking(mockServiceProvider, startDate, endDate)).thenReturn(false);

        // When & Then
        BookingRecordException exception = assertThrows(BookingRecordException.class,
                () -> classUnderTest.createBooking(bookingRequest));

        // Additional assertions
        assertEquals("Service provider unavailable for selected times", exception.getMessage());

        // Verify method calls
        verify(serviceProviderService).getServiceProvider(serviceProviderId);
        verify(serviceProviderService).canAcceptBooking(mockServiceProvider, startDate, endDate);
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(paymentService, never()).getStripePaymentSession(any(Booking.class));
        verify(mapper, never()).bookingToDto(any(Booking.class));
    }

    @Test
    public void NoPaymentReferenceIfStripeException() throws StripeException, ServiceProviderNotFoundException, BookingRecordException {

        ServiceProvider mockServiceProvider = TestUtils.getTestServiceProvider("Test1");
        mockServiceProvider.setUserAccountRef(serviceProviderId);

        Booking found = mock(Booking.class);
        when(bookingRepository.findByBookingId(bookingRequest.getBookingId())).thenReturn(Optional.ofNullable(found));

//        when(serviceProviderService.getServiceProvider(serviceProviderId)).thenReturn(mockServiceProvider);
//        when(serviceProviderService.canAcceptBooking(mockServiceProvider, expectedStart, expectedEnd)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(mock(Booking.class));
        when(mapper.bookingToDto(any(Booking.class))).thenReturn(new BookingDto());
        when(paymentService.getStripePaymentSession(any(Booking.class))).thenReturn(null);

        //When
        BookingDto bookingResponse = classUnderTest.confirmBooking(bookingRequest);

        //then
        assertNotNull(bookingResponse);
        assertNotEquals(bookingResponse.getPaymentSession(), MOCK_STRIPE_SESSION);
        assertNull(bookingResponse.getPaymentSession());
        verify(bookingRepository, times(1)).save(any(Booking.class));
//        verify(serviceProviderService).getServiceProvider(serviceProviderId);
//        verify(serviceProviderService).canAcceptBooking(mockServiceProvider, expectedStart, expectedEnd);
        verify(paymentService).getStripePaymentSession(any(Booking.class));
        verify(mapper).bookingToDto(any(Booking.class));
    }

    @Test
    public void confirmBooking() throws ServiceProviderNotFoundException, StripeException, BookingRecordException {
        //Given
        ServiceProvider mockServiceProvider = TestUtils.getTestServiceProvider("Test1");
        mockServiceProvider.setUserAccountRef(serviceProviderId);

        BookingDto bookingdto = new BookingDto();
        bookingdto.setPaymentSession(MOCK_STRIPE_SESSION);

        Booking mockBooking = mock(Booking.class);

        when(bookingRepository.findByBookingId(bookingRequest.getBookingId()))
                .thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        when(mapper.bookingToDto(mockBooking)).thenReturn(bookingdto);
        when(paymentService.getStripePaymentSession(any(Booking.class))).thenReturn(MOCK_STRIPE_SESSION);

        //When
        BookingDto result = classUnderTest.confirmBooking(bookingRequest);

        //Then
        assertEquals(MOCK_STRIPE_SESSION, result.getPaymentSession());
//        assertEquals(10L, result.getTotalServiceCost());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(paymentService).getStripePaymentSession(any(Booking.class));
        verify(mapper).bookingToDto(any(Booking.class));

    }


    @Test
    public void viewUpcomingEvents() {
        LocalDateTime testDate = LocalDateTime.now().plus(1, ChronoUnit.DAYS);

        Booking booking1 = Booking.builder().bookingDate(testDate).build();
//        Booking booking2 = Booking.builder().bookingDate(L).build();
        BookingListEntry bookingListEntry = BookingListEntry.builder().bookingDate(testDate.toLocalDate().toString()).build();

        when(bookingRepository.findUpcomingBookingsForProvider(eq(serviceProviderId), any(LocalDateTime.class))).thenReturn(List.of(booking1));
        when(mapper.bookingToBookingListEntry(booking1)).thenReturn(bookingListEntry);

        List<BookingListEntry> result = classUnderTest.getUpcomingBookings(serviceProviderId);

        assertEquals(1, result.size());

        verify(bookingRepository).findUpcomingBookingsForProvider(eq(serviceProviderId), any(LocalDateTime.class));
        verify(mapper).bookingToBookingListEntry(booking1);
    }

}