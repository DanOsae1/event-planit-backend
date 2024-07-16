package com.osaebros.eventplanner.service;

import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.repository.ServiceProviderRepository;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.model.ServiceProviderProfileModel;

import java.time.LocalDateTime;

public interface ServiceProviderService {

    ServiceProviderRepository serviceProviderRepository();

    default ServiceProvider getServiceProvider(String serviceProviderReferenceId) throws ServiceProviderNotFoundException {

        return serviceProviderRepository().findByUserAccountRef(serviceProviderReferenceId)
                .orElseThrow(() -> new ServiceProviderNotFoundException("Could not find requested provider"));
    }

    ServiceProviderProfileModel getServiceProviderProfile(String serviceProviderReferenceId) throws ServiceProviderNotFoundException;

    void sendEventToExternalCalendar(BookingDto bookingDto);

    Boolean canAcceptBooking(ServiceProvider serviceProvider, LocalDateTime requestedStartTime, LocalDateTime rquestedEndTime);
}
