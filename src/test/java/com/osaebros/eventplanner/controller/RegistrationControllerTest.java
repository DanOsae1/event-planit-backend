package com.osaebros.eventplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osaebros.eventplanner.config.WebSecurityConfigTest;
import com.osaebros.eventplanner.exception.AuthException;
import com.osaebros.eventplanner.exception.UserAccountExistsException;
import com.osaebros.eventplanner.model.Offering;
import com.osaebros.eventplanner.model.RegisterRequest;
import com.osaebros.eventplanner.model.RegisterResponse;
import com.osaebros.eventplanner.model.ServiceProviderRegistrationRequest;
import com.osaebros.eventplanner.repository.dto.UserAccountDto;
import com.osaebros.eventplanner.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegistrationController.class)
@Import(WebSecurityConfigTest.class)

public class RegistrationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserManagementService registrationService;

    RegisterRequest request;


    @BeforeEach
    public void setup() {
        request = RegisterRequest.builder()
                .firstName("firstname")
                .lastName("lastname")
                .email("email@email.com")
                .password("password")
                .phoneNumber("07111111111")
                .isServiceProvider(true)
                .build();
    }


    @Test
    public void testCreateUserAccountSuccess() throws Exception {

        // Populate request object with valid data

        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setEmail("email@email.com");
        userAccountDto.setFirstName("firstname");
        userAccountDto.setLastName("lastname");
        userAccountDto.setPhoneNumber("07111111111");
        userAccountDto.setUserAccountRef("email@email.com");

        when(registrationService.createAccount(request)).thenReturn(userAccountDto);

        mockMvc.perform(post("/v1/registration/create-account")
                        .header("Idempotent-Key", "test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userAccountRef").exists())
                .andExpect(jsonPath("$.email").value(userAccountDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userAccountDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userAccountDto.getLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(userAccountDto.getPhoneNumber()));


        verify(registrationService).createAccount(any(RegisterRequest.class));
    }

    @Test
    public void testServiceProviderEnterDetails() throws Exception {

        ServiceProviderRegistrationRequest serviceProviderRegistrationRequest = new ServiceProviderRegistrationRequest();
        serviceProviderRegistrationRequest.setBio("bio");
        serviceProviderRegistrationRequest.setLinks(Map.of("Instagram", "www.instagram.com"));
        serviceProviderRegistrationRequest.setOffering(String.valueOf(Offering.DJ));
        serviceProviderRegistrationRequest.setHourlyRate(BigDecimal.valueOf(10L));
        serviceProviderRegistrationRequest.setTags(List.of("tag"));
        serviceProviderRegistrationRequest.setRequirements("No latebookings");
        serviceProviderRegistrationRequest.setAgreeToNylas(false);
        serviceProviderRegistrationRequest.setImages(Map.of("profile", "images"));


        RegisterResponse response = new RegisterResponse("userAccountRef", "email@email.com");
        response.setEmail("email@email.com");
        response.setUserAccountRef("serviceproviderid");

        when(registrationService.createServiceProviderAccount(serviceProviderRegistrationRequest)).thenReturn(response);

        mockMvc.perform(put("/v1/registration/create-account-service-provider-details")
                        .header("Idempotent-Key", "test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(serviceProviderRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userAccountRef").exists())
                .andExpect(jsonPath("$.email").value(response.getEmail()));

        verify(registrationService).createServiceProviderAccount(serviceProviderRegistrationRequest);
    }


    @Test
    public void testCreateUserAccountUserExists() throws Exception {

        // Populate request object with valid data
        doThrow(new UserAccountExistsException("User already exists")).when(registrationService).createAccount(any(RegisterRequest.class));

        mockMvc.perform(post("/v1/registration/create-account")
                        .header("Idempotent-Key", "test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void testCreateUserAccountInternalServerError() throws Exception {

        // Populate request object with valid data
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("someemail")
                .build();
        doThrow(new AuthException("Test Auth")).when(registrationService).createAccount(registerRequest);

        mockMvc.perform(post("/v1/registration/create-account")
                        .header("Idempotent-Key", "test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
        ;
    }
}