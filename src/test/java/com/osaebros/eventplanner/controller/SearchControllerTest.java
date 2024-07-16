package com.osaebros.eventplanner.controller;

import com.osaebros.eventplanner.config.WebSecurityConfigTest;
import com.osaebros.eventplanner.model.ServiceProviderListEntryModel;
import com.osaebros.eventplanner.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SearchController.class)
@Import(WebSecurityConfigTest.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchServiceImpl searchService;


    @Test
    public void testSearch() throws Exception {
        // Mock setup
        Page<ServiceProviderListEntryModel> mockPage = new PageImpl<>(Arrays.asList(new ServiceProviderListEntryModel()));
        when(searchService.search(any(Pageable.class), eq("test"), any(Double.class), any(Double.class), eq(10.0))).thenReturn(mockPage);

        // Execute and Verify
        MvcResult result = mockMvc.perform(get("/v1/search")
                        .param("searchTerm", "test")
                        .param("latitude", "34.05")
                        .param("longitude", "-118.25")
                        .param("searchRadius", "10.0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andReturn();

        // Print response content for debugging
        System.out.println("Response Content: " + result.getResponse().getContentAsString());

        // Verify interactions
        verify(searchService).search(any(Pageable.class), eq("test"), any(Double.class), any(Double.class), eq(10.0));
    }

}