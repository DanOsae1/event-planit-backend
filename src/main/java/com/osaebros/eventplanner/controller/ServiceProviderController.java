package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.ServiceProviderProfileModel;
import com.osaebros.eventplanner.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("v1/service-provider")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    @GetMapping("/{userAccountRef}")
    public ResponseEntity<ServiceProviderProfileModel> getServiceProvider(@PathVariable("userAccountRef") String userAccountRef) throws ServiceProviderNotFoundException {
        long start = System.currentTimeMillis();
        log.info("Received request for {}", userAccountRef);
        ServiceProviderProfileModel serviceProvider = serviceProviderService.getServiceProviderProfile(userAccountRef);
        log.info("Completed search for \"{}\".Time taken {}ms ", serviceProvider, System.currentTimeMillis() - start);
        return ResponseEntity
                .ok(serviceProvider);
    }
}
