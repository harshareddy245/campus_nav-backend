package com.campusnav.service;

import com.campusnav.dto.RouteRequest;
import com.campusnav.dto.RouteResponse;
import com.campusnav.entity.Building;
import com.campusnav.entity.CampusLocation;
import com.campusnav.entity.RouteConnection;
import com.campusnav.repository.CampusLocationRepository;
import com.campusnav.repository.RouteConnectionRepository;
import com.campusnav.repository.SavedRouteRepository;
import com.campusnav.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for NavigationService and Dijkstra pathfinding.
 * Matches Week 11-12 syllabus: Unit Testing (JUnit, Mockito).
 */
@ExtendWith(MockitoExtension.class)
public class NavigationServiceTest {

    @Mock
    private CampusLocationRepository locationRepository;

    @Mock
    private RouteConnectionRepository connectionRepository;

    @Mock
    private SavedRouteRepository savedRouteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationService locationService;

    @Mock
    private AuditLogService auditLogService;

    @Spy
    private DijkstraAlgorithm dijkstraAlgorithm;

    @InjectMocks
    private NavigationService navigationService;

    private CampusLocation locA;
    private CampusLocation locB;
    private CampusLocation locC;
    private RouteConnection edgeAB;
    private RouteConnection edgeBC;

    @BeforeEach
    void setUp() {
        Building b = new Building("Block A", "BLK-A", "CS Dept", 3, 100, 100, 50, 50);
        b.setId(1L);

        locA = new CampusLocation("Entrance", "ENT-01", "GATE", 0, 100, 100, "Main Entry", "24/7", "Security", true, b);
        locA.setId(1L);

        locB = new CampusLocation("Courtyard", "COURT", "FACILITY", 0, 200, 100, "Garden", "24/7", "Staff", true, b);
        locB.setId(2L);

        locC = new CampusLocation("Java Lab", "LAB-301", "LAB", 1, 300, 100, "Lab", "8-5", "Lab Incharge", true, b);
        locC.setId(3L);

        edgeAB = new RouteConnection(locA, locB, 50.0, 40, "WALKWAY", true, "Walk east to courtyard");
        edgeBC = new RouteConnection(locB, locC, 60.0, 48, "CORRIDOR", true, "Walk from courtyard to Java lab");
    }

    @Test
    void testCalculateRoute_SuccessfulShortestPath() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(locA));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(locC));
        when(connectionRepository.findAll()).thenReturn(List.of(edgeAB, edgeBC));

        RouteRequest request = new RouteRequest(1L, 3L, false);
        RouteResponse response = navigationService.calculateRoute(request, "JUnit-Test");

        assertNotNull(response);
        assertEquals(110.0, response.getTotalDistanceMeters());
        assertEquals(2, response.getSteps().size());
        assertEquals(3, response.getPathNodeIds().size());
        assertTrue(response.isWheelchairAccessible());
    }

    @Test
    void testCalculateRoute_SameSourceAndDestination() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(locA));

        RouteRequest request = new RouteRequest(1L, 1L, false);
        RouteResponse response = navigationService.calculateRoute(request, "JUnit-Test");

        assertNotNull(response);
        assertEquals(0.0, response.getTotalDistanceMeters());
        assertEquals(0, response.getEstimatedWalkingMinutes());
    }
}
