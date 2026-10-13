package com.campusnav.service;

import com.campusnav.dto.LocationDto;
import com.campusnav.entity.Building;
import com.campusnav.entity.CampusLocation;
import com.campusnav.entity.Facility;
import com.campusnav.exception.BadRequestException;
import com.campusnav.exception.ResourceNotFoundException;
import com.campusnav.repository.BuildingRepository;
import com.campusnav.repository.CampusLocationRepository;
import com.campusnav.repository.FacilityRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {

    private final CampusLocationRepository locationRepository;
    private final BuildingRepository buildingRepository;
    private final FacilityRepository facilityRepository;

    public LocationService(CampusLocationRepository locationRepository,
                           BuildingRepository buildingRepository,
                           FacilityRepository facilityRepository) {
        this.locationRepository = locationRepository;
        this.buildingRepository = buildingRepository;
        this.facilityRepository = facilityRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "locations", key = "'all'")
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<LocationDto> getLocationsPaged(String category, String query, Pageable pageable) {
        return locationRepository.filterLocationsPaged(
                category != null && !category.isBlank() ? category : null,
                query != null && !query.isBlank() ? query : null,
                pageable
        ).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public LocationDto getLocationById(Long id) {
        CampusLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "id", id));
        return mapToDto(location);
    }

    @Transactional(readOnly = true)
    public List<LocationDto> searchLocations(String query) {
        return locationRepository.searchLocations(query).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocationDto> getLocationsByCategory(String category) {
        return locationRepository.findByCategoryIgnoreCase(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocationDto> getLocationsByBuilding(Long buildingId) {
        return locationRepository.findByBuildingId(buildingId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"locations", "routes"}, allEntries = true)
    public LocationDto createLocation(LocationDto dto) {
        if (locationRepository.findByCode(dto.getCode()).isPresent()) {
            throw new BadRequestException("Location with code '" + dto.getCode() + "' already exists");
        }

        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", dto.getBuildingId()));

        CampusLocation location = new CampusLocation(
                dto.getName(),
                dto.getCode(),
                dto.getCategory(),
                dto.getFloor(),
                dto.getMapX(),
                dto.getMapY(),
                dto.getDescription(),
                dto.getOperatingHours(),
                dto.getContactPerson(),
                dto.isWheelchairAccessible(),
                building
        );

        if (dto.getFacilities() != null && !dto.getFacilities().isEmpty()) {
            Set<Facility> facilities = new HashSet<>();
            for (String facName : dto.getFacilities()) {
                facilityRepository.findByName(facName).ifPresent(facilities::add);
            }
            location.setFacilities(facilities);
        }

        CampusLocation saved = locationRepository.save(location);
        return mapToDto(saved);
    }

    @CacheEvict(value = {"locations", "routes"}, allEntries = true)
    public LocationDto updateLocation(Long id, LocationDto dto) {
        CampusLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "id", id));

        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", dto.getBuildingId()));

        location.setName(dto.getName());
        location.setCategory(dto.getCategory());
        location.setFloor(dto.getFloor());
        location.setMapX(dto.getMapX());
        location.setMapY(dto.getMapY());
        location.setDescription(dto.getDescription());
        location.setOperatingHours(dto.getOperatingHours());
        location.setContactPerson(dto.getContactPerson());
        location.setWheelchairAccessible(dto.isWheelchairAccessible());
        location.setBuilding(building);

        if (dto.getFacilities() != null) {
            Set<Facility> facilities = new HashSet<>();
            for (String facName : dto.getFacilities()) {
                facilityRepository.findByName(facName).ifPresent(facilities::add);
            }
            location.setFacilities(facilities);
        }

        CampusLocation updated = locationRepository.save(location);
        return mapToDto(updated);
    }

    @CacheEvict(value = {"locations", "routes"}, allEntries = true)
    public void deleteLocation(Long id) {
        CampusLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "id", id));
        locationRepository.delete(location);
    }

    public LocationDto mapToDto(CampusLocation location) {
        LocationDto dto = new LocationDto();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setCode(location.getCode());
        dto.setCategory(location.getCategory());
        dto.setFloor(location.getFloor());
        dto.setMapX(location.getMapX());
        dto.setMapY(location.getMapY());
        dto.setDescription(location.getDescription());
        dto.setOperatingHours(location.getOperatingHours());
        dto.setContactPerson(location.getContactPerson());
        dto.setWheelchairAccessible(location.isWheelchairAccessible());

        if (location.getBuilding() != null) {
            dto.setBuildingId(location.getBuilding().getId());
            dto.setBuildingName(location.getBuilding().getName());
            dto.setBuildingCode(location.getBuilding().getCode());
        }

        if (location.getFacilities() != null) {
            dto.setFacilities(location.getFacilities().stream()
                    .map(Facility::getName)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}
