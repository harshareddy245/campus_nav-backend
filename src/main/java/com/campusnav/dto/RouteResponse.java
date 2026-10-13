package com.campusnav.dto;

import java.util.ArrayList;
import java.util.List;

public class RouteResponse {

    private LocationDto source;
    private LocationDto destination;
    private double totalDistanceMeters;
    private int estimatedWalkingMinutes;
    private boolean isWheelchairAccessible;
    private List<RouteStepDto> steps = new ArrayList<>();
    private List<Long> pathNodeIds = new ArrayList<>();
    private String summary;

    public RouteResponse() {
    }

    public LocationDto getSource() {
        return source;
    }

    public void setSource(LocationDto source) {
        this.source = source;
    }

    public LocationDto getDestination() {
        return destination;
    }

    public void setDestination(LocationDto destination) {
        this.destination = destination;
    }

    public double getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void setTotalDistanceMeters(double totalDistanceMeters) {
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public int getEstimatedWalkingMinutes() {
        return estimatedWalkingMinutes;
    }

    public void setEstimatedWalkingMinutes(int estimatedWalkingMinutes) {
        this.estimatedWalkingMinutes = estimatedWalkingMinutes;
    }

    public boolean isWheelchairAccessible() {
        return isWheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        isWheelchairAccessible = wheelchairAccessible;
    }

    public List<RouteStepDto> getSteps() {
        return steps;
    }

    public void setSteps(List<RouteStepDto> steps) {
        this.steps = steps;
    }

    public List<Long> getPathNodeIds() {
        return pathNodeIds;
    }

    public void setPathNodeIds(List<Long> pathNodeIds) {
        this.pathNodeIds = pathNodeIds;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
