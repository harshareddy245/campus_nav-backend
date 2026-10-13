package com.campusnav.dto;

import jakarta.validation.constraints.NotNull;

public class ConnectionDto {

    private Long id;

    @NotNull(message = "From Location ID is required")
    private Long fromLocationId;
    private String fromLocationName;

    @NotNull(message = "To Location ID is required")
    private Long toLocationId;
    private String toLocationName;

    @NotNull(message = "Distance is required")
    private Double distanceMeters;

    private Integer walkingTimeSeconds;
    private String pathType; // WALKWAY, CORRIDOR, STAIRS, ELEVATOR, OPEN_GROUND
    private boolean isWheelchairAccessible = true;
    private String directionsText;
    private boolean bidirectional = true;

    public ConnectionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Long fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public String getFromLocationName() {
        return fromLocationName;
    }

    public void setFromLocationName(String fromLocationName) {
        this.fromLocationName = fromLocationName;
    }

    public Long getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(Long toLocationId) {
        this.toLocationId = toLocationId;
    }

    public String getToLocationName() {
        return toLocationName;
    }

    public void setToLocationName(String toLocationName) {
        this.toLocationName = toLocationName;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public Integer getWalkingTimeSeconds() {
        return walkingTimeSeconds;
    }

    public void setWalkingTimeSeconds(Integer walkingTimeSeconds) {
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

    public boolean isBidirectional() {
        return bidirectional;
    }

    public void setBidirectional(boolean bidirectional) {
        this.bidirectional = bidirectional;
    }
}
