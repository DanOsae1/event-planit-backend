package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.exception.ServiceProviderNotFoundException;
import com.osaebros.eventplanner.model.ServiceProviderProfileModel;
import com.osaebros.eventplanner.repository.ServiceProviderRepository;
import com.osaebros.eventplanner.repository.mapping.ServiceProviderMapper;
import com.osaebros.eventplanner.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceProviderServiceImplTest {

    @InjectMocks
    ServiceProviderServiceImpl classUnderTest;
    @Mock
    ServiceProviderRepository serviceProviderRepository;
    @Mock
    ServiceProviderMapper serviceProviderMapper;

    @Test
    public void getServiceProividerProfile() throws ServiceProviderNotFoundException {
        String ref = "ref";
        ServiceProvider serviceProvider = TestUtils.getTestServiceProvider("someone");
        Optional<ServiceProvider> optionalServiceProvider = Optional.of(serviceProvider);
        ServiceProviderProfileModel serviceProviderProfileModel = new ServiceProviderProfileModel();
        serviceProviderProfileModel.setUsername("someone");

        when(serviceProviderRepository.findByUserAccountRef(ref)).thenReturn(optionalServiceProvider);
        when(serviceProviderMapper.serviceProviderToProfile(serviceProvider)).thenReturn(serviceProviderProfileModel);

        ServiceProviderProfileModel actual = classUnderTest.getServiceProviderProfile(ref);

        assertEquals(serviceProvider.getUsername(), actual.getUsername());
        verify(serviceProviderRepository).findByUserAccountRef(ref);
        verify(serviceProviderMapper).serviceProviderToProfile(serviceProvider);
    }


}