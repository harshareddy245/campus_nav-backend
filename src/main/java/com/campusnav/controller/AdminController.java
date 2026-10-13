package com.campusnav.controller;

import com.campusnav.dto.ConnectionDto;
import com.campusnav.dto.LocationDto;
import com.campusnav.dto.UserSummaryDto;
import com.campusnav.entity.NavigationAuditLog;
import com.campusnav.service.AuditLogService;
import com.campusnav.service.LocationService;
import com.campusnav.service.NavigationService;
import com.campusnav.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Secured Admin Controller for managing campus infrastructure, locations,
 * pathways, and viewing search analytics.
 * Matches Week 6: @PreAuthorize("hasRole('ADMIN')") and project Abstract.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Admin CRUD operations on locations, pathways, and audit logs")
public class AdminController {

    private final LocationService locationService;
    private final NavigationService navigationService;
    private final AuditLogService auditLogService;
    private final UserService userService;

    public AdminController(LocationService locationService,
                           NavigationService navigationService,
                           AuditLogService auditLogService,
                           UserService userService) {
        this.locationService = locationService;
        this.navigationService = navigationService;
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @PostMapping("/locations")
    @Operation(summary = "Add a new campus location")
    public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationDto dto) {
        LocationDto created = locationService.createLocation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/locations/{id}")
    @Operation(summary = "Update an existing campus location")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationDto dto) {
        LocationDto updated = locationService.updateLocation(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/locations/{id}")
    @Operation(summary = "Delete a campus location")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/connections")
    @Operation(summary = "Create a pathway connection between two locations")
    public ResponseEntity<ConnectionDto> createConnection(@Valid @RequestBody ConnectionDto dto) {
        ConnectionDto created = navigationService.createConnection(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/connections/{id}")
    @Operation(summary = "Remove a pathway connection")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long id) {
        navigationService.deleteConnection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Retrieve recent navigation search audit logs")
    public ResponseEntity<List<NavigationAuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogService.getRecentSearches());
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered accounts")
    public ResponseEntity<List<UserSummaryDto>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete an account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
