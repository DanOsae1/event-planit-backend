package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.ServiceProviderRegistrationRequest;
import com.osaebros.eventplanner.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v1/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping("/nylas-authenticate-calender-system")
    public String authenticateCalendarSystem(@RequestBody ServiceProviderRegistrationRequest registrationRequest) {
        try {
            String auth = calendarService.authenticateUserCalender(registrationRequest.getEmailAddress());
            return format("redirect:/%s", auth);
        } catch (ServiceProviderNotFoundException e) {
            log.error(e.getMessage());
            return "redirect:/";
        }
    }
}
