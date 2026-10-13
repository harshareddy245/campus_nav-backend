package com.campusnav.dto;

public class RouteStepDto {

    private int stepNumber;
    private String instruction;
    private String fromLocationName;
    private String toLocationName;
    private double distanceMeters;
    private int walkingTimeSeconds;
    private String pathType;

    public RouteStepDto() {
    }

    public RouteStepDto(int stepNumber, String instruction, String fromLocationName,
                        String toLocationName, double distanceMeters, int walkingTimeSeconds,
                        String pathType) {
        this.stepNumber = stepNumber;
        this.instruction = instruction;
        this.fromLocationName = fromLocationName;
        this.toLocationName = toLocationName;
        this.distanceMeters = distanceMeters;
        this.walkingTimeSeconds = walkingTimeSeconds;
        this.pathType = pathType;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getFromLocationName() {
        return fromLocationName;
    }

    public void setFromLocationName(String fromLocationName) {
        this.fromLocationName = fromLocationName;
    }

    public String getToLocationName() {
        return toLocationName;
    }

    public void setToLocationName(String toLocationName) {
        this.toLocationName = toLocationName;
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
}
