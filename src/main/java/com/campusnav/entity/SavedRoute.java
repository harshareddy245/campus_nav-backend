package com.campusnav.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * SavedRoute lets registered users bookmark their frequent campus journeys.
 * Demonstrates Week 4: @ManyToOne relationships to User and CampusLocation.
 */
@Entity
@Table(name = "saved_routes")
public class SavedRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_location_id", nullable = false)
    private CampusLocation sourceLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private CampusLocation destinationLocation;

    @Column(nullable = false, length = 120)
    private String title;

    private double totalDistanceMeters;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public SavedRoute() {
    }

    public SavedRoute(User user, CampusLocation sourceLocation, CampusLocation destinationLocation,
                      String title, double totalDistanceMeters) {
        this.user = user;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.title = title;
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CampusLocation getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(CampusLocation sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public CampusLocation getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(CampusLocation destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void setTotalDistanceMeters(double totalDistanceMeters) {
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
