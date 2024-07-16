package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.GeneralCustomerAccount;
import com.osaebros.eventplanner.entity.Link;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.entity.UserAccount;
import com.osaebros.eventplanner.exception.AuthException;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.exception.UserAccountExistsException;
import com.osaebros.eventplanner.model.Credentials;
import com.osaebros.eventplanner.model.LoginResponse;
import com.osaebros.eventplanner.model.Offering;
import com.osaebros.eventplanner.model.RegisterRequest;
import com.osaebros.eventplanner.model.RegisterResponse;
import com.osaebros.eventplanner.model.ServiceProviderRegistrationRequest;
import com.osaebros.eventplanner.repository.GeneralCustomerAccountRepository;
import com.osaebros.eventplanner.repository.dto.UserAccountDto;
import com.osaebros.eventplanner.repository.mapping.UserAccountMapper;
import com.osaebros.eventplanner.service.AuthService;
import com.osaebros.eventplanner.service.CalendarService;
import com.osaebros.eventplanner.service.ServiceProviderService;
import com.osaebros.eventplanner.service.UserManagementService;
import com.osaebros.eventplanner.utils.GeometryUtils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final AuthService authService;
    private final ServiceProviderService serviceProviderService;
    private final GeometryUtils geometryUtils;
    private final GeneralCustomerAccountRepository generalCustomerAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final CalendarService calendarService;
    private String FRONTEND_REDIRECT_URI;
    @Value("application.frontend_url")
    private String FRONTEND_HOME_URI;

    @PostConstruct
    public void storeUsers() {
        if (serviceProviderService.serviceProviderRepository().findAll().isEmpty()) {
            ServiceProvider serviceProvider = ServiceProvider
                    .builder()
                    .username("test")
                    .hourlyRate(BigDecimal.valueOf(1000L))
                    .email("test@mail.com")
                    .offering(Offering.VENUE)
                    .location(geometryUtils
                            .createPoint(-0.119519, 51.503399))
                    .build();

            ServiceProvider serviceProvider2 = ServiceProvider
                    .builder()
                    .username("test2")
                    .hourlyRate(BigDecimal.valueOf(1000L))
                    .email("test@mail.com")
                    .offering(Offering.VENUE)
                    .location(geometryUtils
                            .createPoint(-0.119519, 51.503399))
                    .build();

            serviceProviderService.serviceProviderRepository().save(serviceProvider);
            serviceProviderService.serviceProviderRepository().save(serviceProvider2);
        }
    }

    @Override
    public LoginResponse login(Credentials credentials) throws AuthException {
        ResponseEntity<LoginResponse> loginResponse = authService.login(credentials);
        if (!loginResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Could not log in user");
            throw new AuthException("Could not log in user");
        }
        return loginResponse.getBody();
    }

    //    TODO:- Create account for admin users;
    @Override
    @Transactional
    public UserAccountDto createAccount(RegisterRequest registrationRequest) throws UserAccountExistsException, AuthException {
        //Validate incoming fields
        assert registrationRequest.getEmail().length() > 1;

        log.info("Checking if user exists");
        Credentials credentials = getCredentialsFromRequest(registrationRequest);
        Boolean doesUserExist = authService.doesUserExists(credentials);

        if (doesUserExist) {
            throw new UserAccountExistsException("The user already exists under the email");
        }

        UserAccount userAccount;
        log.info("Creating user");
        String keyCloakUserId = authService.createUser(registrationRequest);

        if (registrationRequest.getIsServiceProvider()) {
            log.info("Creating service provider account");
            ServiceProvider serviceProvider = ServiceProvider.builder()
                    .email(credentials.getEmailAddress())
                    .firstName(registrationRequest.getFirstName())
                    .lastName(registrationRequest.getLastName())
                    .phoneNumber(registrationRequest.getPhoneNumber())
                    .KeycloakId(keyCloakUserId)
                    .build();
            userAccount = serviceProviderService.serviceProviderRepository()
                    .save(serviceProvider);
            FRONTEND_REDIRECT_URI = "http://localhost:3000/verified-account/" + credentials.getEmailAddress();
        } else {
            log.info("Creating generic user account");
            GeneralCustomerAccount generalCustomerAccount = GeneralCustomerAccount.builder()
                    .email(credentials.getEmailAddress())
                    .firstName(registrationRequest.getFirstName())
                    .lastName(registrationRequest.getLastName())
                    .phoneNumber(registrationRequest.getPhoneNumber())
                    .KeycloakId(keyCloakUserId)
                    .build();
            userAccount = generalCustomerAccountRepository.save(generalCustomerAccount);
        }
        authService.sendVerificationEmail(keyCloakUserId, FRONTEND_REDIRECT_URI);

        UserAccountDto userAccountDto = userAccountMapper.userAccountToDto(userAccount);

        return userAccountDto;

    }

    @Override
    @Transactional
    public RegisterResponse createServiceProviderAccount(ServiceProviderRegistrationRequest registrationRequest) throws ServiceProviderNotFoundException {
        ServiceProvider serviceProvider = serviceProviderService.serviceProviderRepository()
                .findByEmail(registrationRequest.getEmailAddress())
                .orElseThrow(() -> new ServiceProviderNotFoundException("Did not find service provider with account :" + registrationRequest.getEmailAddress()));

        log.info("Saving service provider account");
        serviceProvider.setBio(registrationRequest.getBio());
        serviceProvider.setTags(registrationRequest.getTags());
        serviceProvider.setLinks(this.buildLinksObject(registrationRequest));
        serviceProvider.setOffering(Offering.getOffering(registrationRequest.getOffering()));
        serviceProvider.setHourlyRate(registrationRequest.getHourlyRate());

        if (Objects.nonNull(registrationRequest.getHomeLocation())) {
            Double longitude = registrationRequest.getHomeLocation().getLon();
            Double latitude = registrationRequest.getHomeLocation().getLat();
            String address = registrationRequest.getHomeLocation().getAddress();
            String postcode = registrationRequest.getHomeLocation().getPostcode();
            serviceProvider.setLocation(geometryUtils.createPoint(longitude, latitude));
        }

        ServiceProvider savedAccount = serviceProviderService.serviceProviderRepository().save(serviceProvider);
        log.info("Saved service provider account");

        return new RegisterResponse(savedAccount.getUserAccountRef(), savedAccount.getEmail());
    }

    private List<Link> buildLinksObject(ServiceProviderRegistrationRequest registrationRequest) {
        return registrationRequest.getLinks().entrySet().stream().map((k) -> Link.builder()
                        .type(k.getKey())
                        .url(k.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void sendEmailVerification(String email) throws UserAccountExistsException {
        Credentials credentials = new Credentials(email, null);
        Boolean doesUserExist = authService.doesUserExists(credentials);
        if (!doesUserExist) {
            throw new UserAccountExistsException("The user does not exists: " + email);
        }
        UserRepresentation userRepresentation = authService.getUser(credentials);
        boolean isServiceProvider = userRepresentation.getAttributes().get("accountType").stream()
                .anyMatch(s -> s.equalsIgnoreCase("service_provider"));
        authService.sendVerificationEmail(userRepresentation.getId(), isServiceProvider ? FRONTEND_REDIRECT_URI : FRONTEND_HOME_URI);
    }

    @Override
    public String confirmAccountForNylasIntegration(String email) {

//        String externalCalKey = calendarService.authenticateUserCalender(email);

        return null;
    }

    @Override
    public UserAccountDto getRegistrationAccount(String email) throws ServiceProviderNotFoundException {
        ServiceProvider serviceProvider = serviceProviderService.serviceProviderRepository().findByEmail(email)
                .orElseThrow(() -> new ServiceProviderNotFoundException("Could not find user " + email));

        return userAccountMapper.userAccountToDto(serviceProvider);
    }


    private Credentials getCredentialsFromRequest(RegisterRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        String password = registrationRequest.getPassword();
        return Credentials.builder()
                .emailAddress(email)
                .password(password)
                .build();
    }
}
