package com.campusnav.controller;

import com.campusnav.dto.ChatRequest;
import com.campusnav.dto.ChatResponse;
import com.campusnav.service.CampusBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for AI CampusBot chatbot with RAG database retrieval.
 * Matches Week 7-8: AI Integration + Chatbot + RAG.
 */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "AI Chatbot", description = "Query CampusBot assistant with RAG-based context injection")
public class ChatbotController {

    private final CampusBotService campusBotService;

    public ChatbotController(CampusBotService campusBotService) {
        this.campusBotService = campusBotService;
    }

    @PostMapping
    @Operation(summary = "Ask CampusBot a question or request navigation directions")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = campusBotService.processChatMessage(request);
        return ResponseEntity.ok(response);
    }
}
