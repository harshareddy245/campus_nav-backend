package com.campusnav.service;

import com.campusnav.dto.AuthRequest;
import com.campusnav.dto.AuthResponse;
import com.campusnav.dto.RegisterRequest;
import com.campusnav.entity.Role;
import com.campusnav.entity.User;
import com.campusnav.entity.UserProfile;
import com.campusnav.exception.BadRequestException;
import com.campusnav.repository.UserRepository;
import com.campusnav.security.JwtTokenProvider;
import com.campusnav.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication service handling user registration, password encoding,
 * authentication verification, and JWT generation.
 * Matches Week 3, Week 5, and Week 6 syllabus.
 */
@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse authenticateUser(AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow();

        Set<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String fullName = user.getProfile() != null ? user.getProfile().getFullName() : user.getUsername();

        return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles, fullName);
    }

    public AuthResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username '" + registerRequest.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email '" + registerRequest.getEmail() + "' is already registered");
        }

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        Set<Role> roles = new HashSet<>();
        if (registerRequest.getRole() != null && registerRequest.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            roles.add(Role.ROLE_ADMIN);
        } else if (registerRequest.getRole() != null && registerRequest.getRole().equalsIgnoreCase("ROLE_FACULTY")) {
            roles.add(Role.ROLE_FACULTY);
        } else {
            roles.add(Role.ROLE_STUDENT);
        }
        user.setRoles(roles);

        UserProfile profile = new UserProfile(
                registerRequest.getFullName(),
                registerRequest.getRollOrEmployeeId(),
                registerRequest.getDepartment(),
                registerRequest.getPhoneNumber()
        );
        user.setProfile(profile);

        User savedUser = userRepository.save(user);

        // Auto authenticate after registration
        return authenticateUser(new AuthRequest(registerRequest.getUsername(), registerRequest.getPassword()));
    }
}
