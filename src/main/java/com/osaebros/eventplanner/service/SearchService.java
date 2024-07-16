package com.osaebros.eventplanner.service;

import com.osaebros.eventplanner.model.ServiceProviderListEntryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    Page<ServiceProviderListEntryModel> search(Pageable pageable, String searchTerm, Double lat, Double lon, Double radiusOfSearch);


}
