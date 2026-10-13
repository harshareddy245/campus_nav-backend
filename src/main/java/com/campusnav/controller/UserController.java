package com.campusnav.controller;

import com.campusnav.dto.UserSummaryDto;
import com.campusnav.security.UserPrincipal;
import com.campusnav.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving currently authenticated user profile.
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "User Profile", description = "Query profile and account details for logged-in user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get profile information of currently authenticated user")
    public ResponseEntity<UserSummaryDto> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserSummaryDto userProfile = userService.getUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(userProfile);
    }
}
