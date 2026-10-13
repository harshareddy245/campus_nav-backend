package com.campusnav.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatResponse {

    private String reply;
    private List<LocationDto> suggestedLocations = new ArrayList<>();
    private RouteResponse suggestedRoute;
    private LocalDateTime timestamp;

    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatResponse(String reply) {
        this.reply = reply;
        this.timestamp = LocalDateTime.now();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<LocationDto> getSuggestedLocations() {
        return suggestedLocations;
    }

    public void setSuggestedLocations(List<LocationDto> suggestedLocations) {
        this.suggestedLocations = suggestedLocations;
    }

    public RouteResponse getSuggestedRoute() {
        return suggestedRoute;
    }

    public void setSuggestedRoute(RouteResponse suggestedRoute) {
        this.suggestedRoute = suggestedRoute;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
