package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.config.WebSecurityConfigTest;
import com.osaebros.eventplanner.model.BookingRequest;
import com.osaebros.eventplanner.model.Location;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
@Import(WebSecurityConfigTest.class)
public class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "user")
    public void createBooking() throws Exception {
        String bookingid = "IDConfiguredAtEnquiry";
        String bookingemail = "me@me.com";
        Double lon = 5D;
        Double lat = 5D;
        BookingRequest bookingRequest = BookingRequest.builder()
                .bookingEmail(bookingemail)
                .userAccountRef("test")
                .date("2024-05-15")
                .time("19:00")
                .duration(3)
                .location(Location.builder()
                        .address("Aburi")
                        .lon(lon)
                        .lon(lat).build())
                .bookingId(bookingid)
                .build();

        BookingDto bookingResponse = new BookingDto();
        bookingResponse.setBookingId(bookingid);

        when(bookingService.createBooking(bookingRequest)).thenReturn(bookingResponse);

        mockMvc.perform(post("/v1/booking")
                        .header("Idempotent-Key", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(bookingid));

        verify(bookingService).createBooking(bookingRequest);
    }

//    @Test
//    @WithMockUser(username = "user")
//    public void confirmBooking() throws Exception {
//        String bookingid = "IDConfiguredAtEnquiry";
//        String bookingemail = "me@me.com";
//        Double lon = 5D;
//        Double lat = 5D;
//        BookingRequest bookingRequest = BookingRequest.builder()
//                .bookingEmail(bookingemail)
//                .serviceProviderId("test")
//                .date("2024-05-15")
//                .time("19:00")
//                .duration(3)
//                .location(Location.builder()
//                        .address("Aburi")
//                        .lon(lon)
//                        .lon(lat).build())
//                .bookingId(bookingid)
//                .build();
//
//        BookingDto bookingResponse = new BookingDto();
//        bookingResponse.setBookingId(bookingid);
//
//        when(bookingService.createBooking(bookingRequest)).thenReturn(bookingResponse);
//
//        mockMvc.perform(post("/v1/booking")
//                        .header("idempotency-key", "test")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(bookingRequest)))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.bookingId").value(bookingid));
//
//        verify(bookingService).createBooking(bookingRequest);
//    }



}