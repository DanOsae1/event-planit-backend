package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.model.ServiceProviderListEntryModel;
import com.osaebros.eventplanner.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<Page<ServiceProviderListEntryModel>> search(Pageable pageable, String searchTerm, Double latitude, Double longitude, Double searchRadius) {
        long start = System.currentTimeMillis();
        Page<ServiceProviderListEntryModel> serviceProviderDtos = searchService.search(pageable, searchTerm, latitude, longitude, searchRadius);
        log.info("Completed search for \"{}\". Results: {}. Time taken {}ms ", searchTerm, serviceProviderDtos.getTotalElements(), System.currentTimeMillis() - start);
        return ResponseEntity
                .ok(serviceProviderDtos);
    }

}
