package com.campusnav.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Audit log recording search queries, route calculations, and response times.
 * Populated asynchronously using Spring's @Async mechanism (Week 9-10).
 */
@Entity
@Table(name = "navigation_audit_logs")
public class NavigationAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceName;

    @Column(nullable = false)
    private String destinationName;

    private double routeDistanceMeters;

    private long executionTimeMs;

    private String userAgent;

    private LocalDateTime timestamp;

    public NavigationAuditLog() {
    }

    public NavigationAuditLog(String sourceName, String destinationName, double routeDistanceMeters,
                              long executionTimeMs, String userAgent) {
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.routeDistanceMeters = routeDistanceMeters;
        this.executionTimeMs = executionTimeMs;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public double getRouteDistanceMeters() {
        return routeDistanceMeters;
    }

    public void setRouteDistanceMeters(double routeDistanceMeters) {
        this.routeDistanceMeters = routeDistanceMeters;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
