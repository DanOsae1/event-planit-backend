package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.config.WebSecurityConfigTest;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.model.ServiceProviderProfileModel;
import com.osaebros.eventplanner.repository.ServiceProviderRepository;
import com.osaebros.eventplanner.repository.mapping.ServiceProviderMapper;
import com.osaebros.eventplanner.service.ServiceProviderService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Import(WebSecurityConfigTest.class)
@WebMvcTest(ServiceProviderController.class)
@ActiveProfiles("test")
public class ServiceProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceProviderService serviceProviderService;

    @MockBean
    private ServiceProviderRepository serviceProviderRepository;

    @MockBean
    private ServiceProviderMapper mapper;

//    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void getServiceProviderProfile() throws Exception {
        String id = "test";
        ServiceProviderProfileModel serviceProviderProfileModel = new ServiceProviderProfileModel();
        serviceProviderProfileModel.setUserAccountRef(id);

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setUserAccountRef(id);

        Optional<ServiceProvider> optionalProvider = Optional.of(serviceProvider);

        when(serviceProviderRepository.findByUserAccountRef(id)).thenReturn(optionalProvider);
        when(serviceProviderService.getServiceProvider(id)).thenReturn(serviceProvider);
        when(mapper.serviceProviderToProfile(serviceProvider)).thenReturn(serviceProviderProfileModel);

        mockMvc.perform(get("/v1/service-provider/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userAccountRef").value(id));

        verify(serviceProviderService).getServiceProvider(id);
        verify(mapper).serviceProviderToProfile(serviceProvider);
    }
}

