package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.Booking;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.BookingStatus;
import com.osaebros.eventplanner.repository.ServiceProviderRepository;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.model.ServiceProviderProfileModel;
import com.osaebros.eventplanner.repository.mapping.ServiceProviderMapper;
import com.osaebros.eventplanner.service.CalendarService;
import com.osaebros.eventplanner.service.ServiceProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceProviderMapper serviceProviderMapper;
    private final CalendarService calendarService;

    @Override
    public ServiceProviderRepository serviceProviderRepository() {
        return this.serviceProviderRepository;
    }

    @Override
    public ServiceProviderProfileModel getServiceProviderProfile(String serviceProviderReferenceId) throws ServiceProviderNotFoundException {
        log.info("Looking for service provider {}", serviceProviderReferenceId);
        ServiceProvider serviceProvider = serviceProviderRepository.findByUserAccountRef(serviceProviderReferenceId).orElseThrow(() -> new ServiceProviderNotFoundException("Service provider does not exist"));
        ServiceProviderProfileModel serviceProviderProfileModel = serviceProviderMapper.serviceProviderToProfile(serviceProvider);
        log.info("Found {}, returning result", serviceProviderProfileModel.getUserAccountRef());
        return serviceProviderProfileModel;
    }

    @Override
    public void sendEventToExternalCalendar(BookingDto bookingDto) {

    }

    @Override
    @Transactional
    public Boolean canAcceptBooking(ServiceProvider serviceProvider, LocalDateTime requestedStartTime, LocalDateTime requestedEndTime) {
        List<Booking> bookings = serviceProvider.getBookings();
        if (bookings.isEmpty()) {
            if (Objects.nonNull(serviceProvider.getNylasExchangeCode())) {
                log.info("Checking external calendar");
                Boolean isAvailable = calendarService.isAvailable(requestedStartTime, requestedEndTime, serviceProvider);
                serviceProviderRepository.save(serviceProvider);
                log.info("External calendar status: Available {}", isAvailable);

                return isAvailable;
            }
            return true;
        } else {
            log.info("Checking local calendar for clashes");
            return bookings.stream()
                    .filter(b -> b.getBookingStatus().equals(BookingStatus.CANCELLED) ||
                            b.getBookingStatus().equals(BookingStatus.COMPLETED))
                    .anyMatch(booking -> booking.getBookingDate().isAfter(requestedStartTime) && booking.getBookingDate().isBefore(requestedEndTime)
                            || booking.getBookingEndDate().isBefore(requestedStartTime) && booking.getBookingEndDate().isAfter(requestedEndTime));
        }
    }
}
