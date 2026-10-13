package com.campusnav.controller;

import com.campusnav.dto.LocationDto;
import com.campusnav.entity.Building;
import com.campusnav.entity.Facility;
import com.campusnav.repository.BuildingRepository;
import com.campusnav.repository.FacilityRepository;
import com.campusnav.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for retrieving campus locations, facilities, and buildings.
 * Matches Week 1, Week 2 (CRUD / GET APIs), and Week 4 (Pagination, Sorting).
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Locations", description = "Query campus nodes, classrooms, laboratories, and buildings")
public class LocationController {

    private final LocationService locationService;
    private final BuildingRepository buildingRepository;
    private final FacilityRepository facilityRepository;

    public LocationController(LocationService locationService,
                              BuildingRepository buildingRepository,
                              FacilityRepository facilityRepository) {
        this.locationService = locationService;
        this.buildingRepository = buildingRepository;
        this.facilityRepository = facilityRepository;
    }

    @GetMapping("/locations")
    @Operation(summary = "Get all locations or filter by pagination and category")
    public ResponseEntity<?> getLocations(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page != null && size != null) {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<LocationDto> pagedLocations = locationService.getLocationsPaged(category, query, pageable);
            return ResponseEntity.ok(pagedLocations);
        }

        if (query != null && !query.isBlank()) {
            return ResponseEntity.ok(locationService.searchLocations(query));
        }

        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(locationService.getLocationsByCategory(category));
        }

        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/locations/{id}")
    @Operation(summary = "Get location details by ID")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping("/locations/building/{buildingId}")
    @Operation(summary = "Get all locations within a specific building")
    public ResponseEntity<List<LocationDto>> getLocationsByBuilding(@PathVariable Long buildingId) {
        return ResponseEntity.ok(locationService.getLocationsByBuilding(buildingId));
    }

    @GetMapping("/buildings")
    @Operation(summary = "List all campus buildings")
    public ResponseEntity<List<Building>> getBuildings() {
        return ResponseEntity.ok(buildingRepository.findAll());
    }

    @GetMapping("/facilities")
    @Operation(summary = "List all campus facility types")
    public ResponseEntity<List<Facility>> getFacilities() {
        return ResponseEntity.ok(facilityRepository.findAll());
    }
}
