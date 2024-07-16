package com.osaebros.eventplanner.repository;

import com.osaebros.eventplanner.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT b FROM Booking b " +
            "JOIN b.provider p " +
            "WHERE p.userAccountRef = :userAccountRef " +
            "AND b.bookingDate >= :startDateTime " +
            "ORDER BY b.bookingDate ASC")
    List<Booking> findUpcomingBookingsForProvider(
            @Param("userAccountRef") String userAccountRef,
            @Param("startDateTime") LocalDateTime startDateTime
    );

    Optional<Booking> findByBookingId(String bookingId);
}