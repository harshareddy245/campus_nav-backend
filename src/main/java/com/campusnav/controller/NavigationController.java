package com.campusnav.controller;

import com.campusnav.dto.ConnectionDto;
import com.campusnav.dto.RouteRequest;
import com.campusnav.dto.RouteResponse;
import com.campusnav.entity.SavedRoute;
import com.campusnav.security.UserPrincipal;
import com.campusnav.service.NavigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling route finding via Dijkstra algorithm,
 * pathway querying, and bookmarking saved routes.
 */
@RestController
@RequestMapping("/api/navigation")
@Tag(name = "Navigation", description = "Calculate routes, query pathways, and manage user saved routes")
public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @PostMapping("/route")
    @Operation(summary = "Calculate shortest or accessible route between two campus nodes")
    public ResponseEntity<RouteResponse> getShortestRoute(
            @Valid @RequestBody RouteRequest request,
            HttpServletRequest servletRequest) {
        String userAgent = servletRequest.getHeader("User-Agent");
        RouteResponse response = navigationService.calculateRoute(request, userAgent != null ? userAgent : "Unknown");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/connections")
    @Operation(summary = "Get all campus pathway connections for graph visualization")
    public ResponseEntity<List<ConnectionDto>> getAllConnections() {
        return ResponseEntity.ok(navigationService.getAllConnections());
    }

    @PostMapping("/save-route")
    @Operation(summary = "Save route to bookmarks for authenticated user")
    public ResponseEntity<SavedRoute> saveRoute(
            @RequestParam Long sourceId,
            @RequestParam Long destinationId,
            @RequestParam String title,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        SavedRoute saved = navigationService.saveUserRoute(userPrincipal.getId(), sourceId, destinationId, title);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/saved-routes")
    @Operation(summary = "Get list of saved routes for current user")
    public ResponseEntity<List<SavedRoute>> getSavedRoutes(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(navigationService.getUserSavedRoutes(userPrincipal.getId()));
    }

    @DeleteMapping("/saved-routes/{id}")
    @Operation(summary = "Delete saved route bookmark")
    public ResponseEntity<Void> deleteSavedRoute(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        navigationService.deleteSavedRoute(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
