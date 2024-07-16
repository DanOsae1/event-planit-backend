package com.osaebros.eventplanner.service;

import com.nylas.models.NylasOAuthError;
import com.nylas.models.NylasSdkTimeoutError;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;

import java.time.LocalDateTime;

public interface CalendarService {

    String authenticateUserCalender(String email) throws ServiceProviderNotFoundException;

    String getServiceProviderGrantId(String email, String authenticationExchangeCode) throws ServiceProviderNotFoundException, NylasOAuthError, NylasSdkTimeoutError;

    Boolean isAvailable(LocalDateTime start, LocalDateTime end, ServiceProvider serviceProvider);
}
