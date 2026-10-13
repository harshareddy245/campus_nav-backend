package com.campusnav.dto;

import jakarta.validation.constraints.NotNull;

public class RouteRequest {

    @NotNull(message = "Source location ID is required")
    private Long sourceLocationId;

    @NotNull(message = "Destination location ID is required")
    private Long destinationLocationId;

    private boolean accessibleOnly = false;

    public RouteRequest() {
    }

    public RouteRequest(Long sourceLocationId, Long destinationLocationId, boolean accessibleOnly) {
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.accessibleOnly = accessibleOnly;
    }

    public Long getSourceLocationId() {
        return sourceLocationId;
    }

    public void setSourceLocationId(Long sourceLocationId) {
        this.sourceLocationId = sourceLocationId;
    }

    public Long getDestinationLocationId() {
        return destinationLocationId;
    }

    public void setDestinationLocationId(Long destinationLocationId) {
        this.destinationLocationId = destinationLocationId;
    }

    public boolean isAccessibleOnly() {
        return accessibleOnly;
    }

    public void setAccessibleOnly(boolean accessibleOnly) {
        this.accessibleOnly = accessibleOnly;
    }
}
