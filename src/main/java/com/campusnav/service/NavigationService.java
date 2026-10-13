package com.campusnav.service;

import com.campusnav.dto.*;
import com.campusnav.entity.CampusLocation;
import com.campusnav.entity.RouteConnection;
import com.campusnav.entity.SavedRoute;
import com.campusnav.entity.User;
import com.campusnav.exception.BadRequestException;
import com.campusnav.exception.ResourceNotFoundException;
import com.campusnav.repository.CampusLocationRepository;
import com.campusnav.repository.RouteConnectionRepository;
import com.campusnav.repository.SavedRouteRepository;
import com.campusnav.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrating graph navigation calculations, route responses,
 * and user bookmarks.
 */
@Service
@Transactional
public class NavigationService {

    private final CampusLocationRepository locationRepository;
    private final RouteConnectionRepository connectionRepository;
    private final SavedRouteRepository savedRouteRepository;
    private final UserRepository userRepository;
    private final DijkstraAlgorithm dijkstraAlgorithm;
    private final LocationService locationService;
    private final AuditLogService auditLogService;

    public NavigationService(CampusLocationRepository locationRepository,
                             RouteConnectionRepository connectionRepository,
                             SavedRouteRepository savedRouteRepository,
                             UserRepository userRepository,
                             DijkstraAlgorithm dijkstraAlgorithm,
                             LocationService locationService,
                             AuditLogService auditLogService) {
        this.locationRepository = locationRepository;
        this.connectionRepository = connectionRepository;
        this.savedRouteRepository = savedRouteRepository;
        this.userRepository = userRepository;
        this.dijkstraAlgorithm = dijkstraAlgorithm;
        this.locationService = locationService;
        this.auditLogService = auditLogService;
    }

    @Cacheable(value = "routes", key = "#request.sourceLocationId + '-' + #request.destinationLocationId + '-' + #request.accessibleOnly")
    public RouteResponse calculateRoute(RouteRequest request, String userAgent) {
        long startTime = System.currentTimeMillis();

        CampusLocation source = locationRepository.findById(request.getSourceLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "sourceLocationId", request.getSourceLocationId()));

        CampusLocation destination = locationRepository.findById(request.getDestinationLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "destinationLocationId", request.getDestinationLocationId()));

        if (source.getId().equals(destination.getId())) {
            RouteResponse sameNodeResponse = new RouteResponse();
            sameNodeResponse.setSource(locationService.mapToDto(source));
            sameNodeResponse.setDestination(locationService.mapToDto(destination));
            sameNodeResponse.setTotalDistanceMeters(0.0);
            sameNodeResponse.setEstimatedWalkingMinutes(0);
            sameNodeResponse.setWheelchairAccessible(true);
            sameNodeResponse.setPathNodeIds(List.of(source.getId()));
            sameNodeResponse.setSummary("You are already at " + source.getName() + "!");
            return sameNodeResponse;
        }

        List<RouteConnection> allConnections = connectionRepository.findAll();

        DijkstraAlgorithm.PathResult pathResult = dijkstraAlgorithm.findShortestPath(
                source, destination, allConnections, request.isAccessibleOnly()
        ).orElseThrow(() -> new BadRequestException(
                "No viable route found between " + source.getName() + " and " + destination.getName() +
                (request.isAccessibleOnly() ? " with wheelchair accessibility constraints." : ".")
        ));

        List<RouteStepDto> steps = new ArrayList<>();
        int stepNum = 1;
        boolean allWheelchair = true;

        for (RouteConnection edge : pathResult.getEdgesUsed()) {
            if (!edge.isWheelchairAccessible()) {
                allWheelchair = false;
            }

            String instruction = edge.getDirectionsText();
            if (instruction == null || instruction.isBlank()) {
                instruction = String.format("Proceed from %s towards %s via %s (%dm)",
                        edge.getFromLocation().getName(),
                        edge.getToLocation().getName(),
                        edge.getPathType() != null ? edge.getPathType().toLowerCase() : "walkway",
                        Math.round(edge.getDistanceMeters()));
            }

            steps.add(new RouteStepDto(
                    stepNum++,
                    instruction,
                    edge.getFromLocation().getName(),
                    edge.getToLocation().getName(),
                    edge.getDistanceMeters(),
                    edge.getWalkingTimeSeconds(),
                    edge.getPathType()
            ));
        }

        // Average walking speed: 75 meters / minute
        int walkingMinutes = (int) Math.max(1, Math.ceil(pathResult.getTotalDistance() / 75.0));

        RouteResponse response = new RouteResponse();
        response.setSource(locationService.mapToDto(source));
        response.setDestination(locationService.mapToDto(destination));
        response.setTotalDistanceMeters(Math.round(pathResult.getTotalDistance() * 10.0) / 10.0);
        response.setEstimatedWalkingMinutes(walkingMinutes);
        response.setWheelchairAccessible(allWheelchair);
        response.setSteps(steps);
        response.setPathNodeIds(pathResult.getPathNodes().stream().map(CampusLocation::getId).collect(Collectors.toList()));
        response.setSummary(String.format("Walk from %s to %s across %d waypoints (%dm, ~%d min)",
                source.getName(), destination.getName(), steps.size(),
                Math.round(pathResult.getTotalDistance()), walkingMinutes));

        long elapsedMs = System.currentTimeMillis() - startTime;

        // Asynchronous audit logging (Week 9-10)
        auditLogService.recordNavigationSearch(source.getName(), destination.getName(),
                pathResult.getTotalDistance(), elapsedMs, userAgent);

        return response;
    }

    public List<ConnectionDto> getAllConnections() {
        return connectionRepository.findAll().stream().map(this::mapConnectionToDto).collect(Collectors.toList());
    }

    @CacheEvict(value = "routes", allEntries = true)
    public ConnectionDto createConnection(ConnectionDto dto) {
        CampusLocation from = locationRepository.findById(dto.getFromLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "fromLocationId", dto.getFromLocationId()));
        CampusLocation to = locationRepository.findById(dto.getToLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "toLocationId", dto.getToLocationId()));

        int walkingSeconds = dto.getWalkingTimeSeconds() != null ? dto.getWalkingTimeSeconds() :
                (int) Math.round((dto.getDistanceMeters() / 1.25));

        RouteConnection edge = new RouteConnection(
                from, to, dto.getDistanceMeters(), walkingSeconds,
                dto.getPathType(), dto.isWheelchairAccessible(), dto.getDirectionsText()
        );
        RouteConnection saved = connectionRepository.save(edge);

        if (dto.isBidirectional()) {
            RouteConnection reverseEdge = new RouteConnection(
                    to, from, dto.getDistanceMeters(), walkingSeconds,
                    dto.getPathType(), dto.isWheelchairAccessible(),
                    "Reverse: return from " + to.getName() + " to " + from.getName()
            );
            connectionRepository.save(reverseEdge);
        }

        return mapConnectionToDto(saved);
    }

    @CacheEvict(value = "routes", allEntries = true)
    public void deleteConnection(Long id) {
        connectionRepository.deleteById(id);
    }

    public SavedRoute saveUserRoute(Long userId, Long sourceId, Long destinationId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        CampusLocation source = locationRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "id", sourceId));
        CampusLocation destination = locationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("CampusLocation", "id", destinationId));

        RouteRequest req = new RouteRequest(sourceId, destinationId, false);
        RouteResponse route = calculateRoute(req, "CampusNav-SavedRoute");

        SavedRoute savedRoute = new SavedRoute(user, source, destination, title, route.getTotalDistanceMeters());
        return savedRouteRepository.save(savedRoute);
    }

    public List<SavedRoute> getUserSavedRoutes(Long userId) {
        return savedRouteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void deleteSavedRoute(Long userId, Long routeId) {
        SavedRoute saved = savedRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("SavedRoute", "id", routeId));
        if (!saved.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to delete saved route");
        }
        savedRouteRepository.delete(saved);
    }

    private ConnectionDto mapConnectionToDto(RouteConnection edge) {
        ConnectionDto dto = new ConnectionDto();
        dto.setId(edge.getId());
        dto.setFromLocationId(edge.getFromLocation().getId());
        dto.setFromLocationName(edge.getFromLocation().getName());
        dto.setToLocationId(edge.getToLocation().getId());
        dto.setToLocationName(edge.getToLocation().getName());
        dto.setDistanceMeters(edge.getDistanceMeters());
        dto.setWalkingTimeSeconds(edge.getWalkingTimeSeconds());
        dto.setPathType(edge.getPathType());
        dto.setWheelchairAccessible(edge.isWheelchairAccessible());
        dto.setDirectionsText(edge.getDirectionsText());
        return dto;
    }
}
