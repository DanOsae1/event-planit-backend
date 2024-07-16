package com.osaebros.eventplanner.service.impl;

import com.nylas.NylasClient;
import com.nylas.models.AccessType;
import com.nylas.models.AuthProvider;
import com.nylas.models.CodeExchangeRequest;
import com.nylas.models.CodeExchangeResponse;
import com.nylas.models.FreeBusyTimeSlot;
import com.nylas.models.FreeBusyType;
import com.nylas.models.GetFreeBusyRequest;
import com.nylas.models.GetFreeBusyResponse;
import com.nylas.models.NylasApiError;
import com.nylas.models.NylasOAuthError;
import com.nylas.models.NylasSdkTimeoutError;
import com.nylas.models.Prompt;
import com.nylas.models.Response;
import com.nylas.models.UrlForAuthenticationConfig;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    @Value("${nylas.client_id}")
    private String clientId;
    @Value("${application.frontend_url}")
    private String redirectUrl;

    private final NylasClient nylasClient;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public String authenticateUserCalender(String email) {
        String clientUrl = format(redirectUrl + "?ep_client=%s", email);
        String authUrl = nylasClient
                .auth()
                .urlForOAuth2(
                        new UrlForAuthenticationConfig.Builder(clientId, clientUrl)
                                .loginHint(email)
                                .build());
//        return authUrl;


        List<String> scope = new ArrayList<>();

        scope.add("https://www.googleapis.com/auth/userinfo.email");

        UrlForAuthenticationConfig config = new UrlForAuthenticationConfig(
                clientId,
                "http://localhost:4567/oauth/exchange",
                AccessType.ONLINE,
                AuthProvider.GOOGLE,
                Prompt.DETECT,
                scope,
                true,
                "sQ6vFQN",
                "<email_to_connect>");

        String url = nylasClient.auth().urlForOAuth2(config);

        return url;

    }


    @Override
    public String getServiceProviderGrantId(String email, String authenticationExchangeCode) throws NylasOAuthError, NylasSdkTimeoutError {
        String clientUrl = format(redirectUrl + "?ep_client=%s", email);
        CodeExchangeRequest codeExchangeRequest = new CodeExchangeRequest.Builder(clientUrl, authenticationExchangeCode, clientId).build();
        CodeExchangeResponse codeExchangeResponse = nylasClient.auth().exchangeCodeForToken(codeExchangeRequest);
        return codeExchangeResponse.getGrantId();
    }

    //    Still return true incase of endpoint error as booking will be managed by us.
    //    Improvement :- Store calendar outcome for easy processing on next check
    @Override
    public Boolean isAvailable(LocalDateTime start, LocalDateTime end, ServiceProvider serviceProvider) {
        if (start == null || end == null || serviceProvider.getEmail() == null || serviceProvider.getExternalCalender().getNylasGrantId() == null) {
            log.error("Invalid input parameters");
            return false;
        }

        ZoneId zoneId = getTimeZone();
        long startTime = start.atZone(zoneId).toEpochSecond();
        long endTime = end.atZone(zoneId).toEpochSecond();
        String email = serviceProvider.getEmail();

        log.debug("Requesting free/busy info for Nylas endpoint - Start: {}, End: {}, Email: {}",
                start, end, email);

        GetFreeBusyRequest request = new GetFreeBusyRequest((int) startTime, (int) endTime, Collections.singletonList(email));

        try {
            Response<List<GetFreeBusyResponse>> response = nylasClient.calendars()
                    .getFreeBusy(serviceProvider.getExternalCalender().getNylasGrantId(), request);

            for (GetFreeBusyResponse res : response.getData()) {
                if (res.getObject() == FreeBusyType.ERROR) {
                    log.error("Error response from Nylas Client");
                    return true;
                } else if (res.getObject() == FreeBusyType.FREE_BUSY) {
                    log.info("Successfully connected to service provider calendar");
                    return isTimeSlotAvailable(res, start, end);
                }
            }
            log.warn("No valid response from Nylas Client");
            return true;

        } catch (NylasSdkTimeoutError | NylasApiError e) {
            log.error("Could not access provider calendar: {}", e.getMessage(), e);
            return true;
        }
    }

    private boolean isTimeSlotAvailable(GetFreeBusyResponse res, LocalDateTime start, LocalDateTime end) {
        GetFreeBusyResponse.FreeBusy freeBusyResponse = (GetFreeBusyResponse.FreeBusy) res;
        List<FreeBusyTimeSlot> timeSlots = freeBusyResponse.getTimeSlots();

        for (FreeBusyTimeSlot timeSlot : timeSlots) {
            LocalDateTime slotStart = toLocalDateTime(timeSlot.getStartTime());
            LocalDateTime slotEnd = toLocalDateTime(timeSlot.getEndTime());

            log.info("Checking timeslot: {} -> {}", slotStart.format(formatter), slotEnd.format(formatter));

            if (!(slotEnd.isBefore(start) || slotStart.isAfter(end))) {
                return false; // There's an overlap, so the time is not available
            }
        }

        return true; // No overlaps found, the time is available
    }

    private ZoneId getTimeZone() {
        // TODO:- Implement method to return the appropriate ZoneId
        return ZoneId.systemDefault();
    }

    private LocalDateTime toLocalDateTime(int startTime) {
        Date date = new Date(startTime * 1000L);
        return date.toInstant()
                .atZone(getTimeZone())
                .toLocalDateTime();
    }


}


