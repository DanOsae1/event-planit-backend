package com.osaebros.eventplanner.service;

import com.osaebros.eventplanner.exception.AuthException;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.exception.UserAccountExistsException;
import com.osaebros.eventplanner.model.Credentials;
import com.osaebros.eventplanner.model.LoginResponse;
import com.osaebros.eventplanner.model.RegisterRequest;
import com.osaebros.eventplanner.model.RegisterResponse;
import com.osaebros.eventplanner.model.ServiceProviderRegistrationRequest;
import com.osaebros.eventplanner.repository.dto.ServiceProviderDto;
import com.osaebros.eventplanner.repository.dto.UserAccountDto;

public interface UserManagementService {

    LoginResponse login(Credentials credentials) throws AuthException;

    UserAccountDto createAccount(RegisterRequest registrationRequest) throws UserAccountExistsException, AuthException, com.osaebros.eventplanner.exception.AuthException;

    RegisterResponse createServiceProviderAccount(ServiceProviderRegistrationRequest registrationRequest) throws ServiceProviderNotFoundException;

    void sendEmailVerification(String email) throws UserAccountExistsException;

    String confirmAccountForNylasIntegration(String email);

    UserAccountDto getRegistrationAccount(String email) throws ServiceProviderNotFoundException;
}
