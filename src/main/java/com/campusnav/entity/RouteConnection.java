package com.campusnav.entity;

import jakarta.persistence.*;

/**
 * RouteConnection represents a directed or navigable pathway segment between two locations.
 * Serves as edges in the campus graph for Dijkstra shortest-path calculations.
 * Demonstrates Week 4: @ManyToOne relational mapping.
 */
@Entity
@Table(name = "route_connections")
public class RouteConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_location_id", nullable = false)
    private CampusLocation fromLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_location_id", nullable = false)
    private CampusLocation toLocation;

    @Column(nullable = false)
    private double distanceMeters;

    private int walkingTimeSeconds;

    @Column(length = 30)
    private String pathType; // WALKWAY, CORRIDOR, STAIRS, ELEVATOR, OPEN_GROUND

    private boolean isWheelchairAccessible = true;

    @Column(length = 255)
    private String directionsText;

    public RouteConnection() {
    }

    public RouteConnection(CampusLocation fromLocation, CampusLocation toLocation,
                           double distanceMeters, int walkingTimeSeconds,
                           String pathType, boolean isWheelchairAccessible,
                           String directionsText) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.distanceMeters = distanceMeters;
        this.walkingTimeSeconds = walkingTimeSeconds;
        this.pathType = pathType;
        this.isWheelchairAccessible = isWheelchairAccessible;
        this.directionsText = directionsText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampusLocation getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(CampusLocation fromLocation) {
        this.fromLocation = fromLocation;
    }

    public CampusLocation getToLocation() {
        return toLocation;
    }

    public void setToLocation(CampusLocation toLocation) {
        this.toLocation = toLocation;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public int getWalkingTimeSeconds() {
        return walkingTimeSeconds;
    }

    public void setWalkingTimeSeconds(int walkingTimeSeconds) {
        this.walkingTimeSeconds = walkingTimeSeconds;
    }

    public String getPathType() {
        return pathType;
    }

    public void setPathType(String pathType) {
        this.pathType = pathType;
    }

    public boolean isWheelchairAccessible() {
        return isWheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        isWheelchairAccessible = wheelchairAccessible;
    }

    public String getDirectionsText() {
        return directionsText;
    }

    public void setDirectionsText(String directionsText) {
        this.directionsText = directionsText;
    }
}
