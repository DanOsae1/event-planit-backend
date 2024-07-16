package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.model.ServiceProviderListEntryModel;
import com.osaebros.eventplanner.repository.ServiceProviderRepository;
import com.osaebros.eventplanner.repository.mapping.ServiceProviderMapper;
import com.osaebros.eventplanner.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchServiceImplTest {

    @InjectMocks
    SearchServiceImpl classUnderTest;

    @Mock
    ServiceProviderRepository serviceProviderRepository;

    @Mock
    ServiceProviderMapper serviceProviderMapper;

    @Test
    public void searchWithSearchTerm() {
        //given
        Pageable pageable = mock(Pageable.class);
        String searchTerm = " test";
        Double lat = 0.3D;
        Double lon = 10.3D;

        ServiceProvider serviceProvider1 = TestUtils.getTestServiceProvider("Test1");
        ServiceProvider serviceProvider2 = TestUtils.getTestServiceProvider("Test2");

        List<ServiceProvider> serviceProviderList = Arrays.asList(serviceProvider1, serviceProvider2);
        Page<ServiceProvider> serviceProviderPage = new PageImpl<>(serviceProviderList, pageable, serviceProviderList.size());

        ServiceProviderListEntryModel dto1 = new ServiceProviderListEntryModel();
        dto1.setUsername("Test1");
        ServiceProviderListEntryModel dto2 = new ServiceProviderListEntryModel();
        dto2.setUsername("Test2");

        when(serviceProviderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(serviceProviderPage);

        when(serviceProviderMapper.serviceProviderToListEntry(any(ServiceProvider.class)))
                .thenReturn(dto1)
                .thenReturn(dto2);

        //when
        Page<ServiceProviderListEntryModel> results = classUnderTest.search(pageable, searchTerm, lat, lon, 10.0);

        // Verify
        assertEquals(2, results.getTotalElements());
        verify(serviceProviderRepository).findAll(any(Specification.class), eq(pageable));
        verify(serviceProviderMapper, times(2)).serviceProviderToListEntry(any(ServiceProvider.class));
    }

}