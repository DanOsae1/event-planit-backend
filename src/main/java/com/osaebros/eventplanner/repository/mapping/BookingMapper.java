package com.osaebros.eventplanner.repository.mapping;

import com.osaebros.eventplanner.entity.Booking;
import com.osaebros.eventplanner.model.BookingListEntry;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mappings({
            @Mapping(source = "bookingDate", target = "date", dateFormat = "yyyy-MM-dd"),
            @Mapping(source = "provider.email", target = "serviceProviderEmail"),
            @Mapping(source = "stripePaymentReference", target = "paymentSession"),
            @Mapping(source = "bookingUserId", target = "userRef"),
            @Mapping(source = "provider.userAccountRef", target = "serviceProviderRef"),
    })
    BookingDto bookingToDto(Booking booking);

    @Mappings({
            @Mapping(source = "provider.profilePicture", target = "serviceProviderProfileImage"),
    })
    BookingListEntry bookingToBookingListEntry(Booking booking);
}
