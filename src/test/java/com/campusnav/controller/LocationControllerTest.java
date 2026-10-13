package com.campusnav.controller;

import com.campusnav.dto.LocationDto;
import com.campusnav.repository.BuildingRepository;
import com.campusnav.repository.FacilityRepository;
import com.campusnav.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration / Web layer test for LocationController.
 * Matches Week 11-12 syllabus: Integration Testing.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private FacilityRepository facilityRepository;

    @Test
    void testGetAllLocationsEndpoint() throws Exception {
        LocationDto dto = new LocationDto();
        dto.setId(10L);
        dto.setName("Central Library");
        dto.setCode("LIB-01");
        dto.setCategory("LIBRARY");

        when(locationService.getAllLocations()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Central Library"))
                .andExpect(jsonPath("$[0].code").value("LIB-01"));
    }
}
