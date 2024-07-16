package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.model.ServiceProviderListEntryModel;
import com.osaebros.eventplanner.repository.ServiceProviderRepository;
import com.osaebros.eventplanner.repository.mapping.ServiceProviderMapper;
import com.osaebros.eventplanner.repository.specifications.SearchSpecification;
import com.osaebros.eventplanner.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceProviderMapper serviceProviderMapper;

    @Override
    public Page<ServiceProviderListEntryModel> search(Pageable pageable, String searchTerm, Double lat, Double lon, Double sizeOfBoundary) {
        //Validate search terms
        long start = System.currentTimeMillis();
        log.info("Searching db with {}, {}, {},{}", searchTerm, lat, lon, sizeOfBoundary);

        Page<ServiceProvider> result = serviceProviderRepository.findAll(
                SearchSpecification.searchForProviders(searchTerm, lat, lon, sizeOfBoundary),
                pageable);

        Page<ServiceProviderListEntryModel> results = result.map(serviceProviderMapper::serviceProviderToListEntry);

        log.info("Search completed {}ms. Result(s) {}", System.currentTimeMillis() - start, results.getTotalElements());

        return results;
    }
}
