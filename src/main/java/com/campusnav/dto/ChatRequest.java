package com.campusnav.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ChatRequest {

    @NotBlank(message = "Prompt cannot be empty")
    private String message;

    private Long currentLocationId;

    public ChatRequest() {
    }

    public ChatRequest(String message, Long currentLocationId) {
        this.message = message;
        this.currentLocationId = currentLocationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCurrentLocationId() {
        return currentLocationId;
    }

    public void setCurrentLocationId(Long currentLocationId) {
        this.currentLocationId = currentLocationId;
    }
}
