package com.campusnav.service;

import com.campusnav.dto.UserSummaryDto;
import com.campusnav.entity.Role;
import com.campusnav.entity.User;
import com.campusnav.exception.ResourceNotFoundException;
import com.campusnav.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToSummaryDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    private UserSummaryDto mapToSummaryDto(User user) {
        String fullName = user.getProfile() != null ? user.getProfile().getFullName() : user.getUsername();
        String department = user.getProfile() != null ? user.getProfile().getDepartment() : "";
        String rollOrEmp = user.getProfile() != null ? user.getProfile().getRollOrEmployeeId() : "";

        return new UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                fullName,
                department,
                rollOrEmp,
                user.getRoles().stream().map(Role::name).collect(Collectors.toSet())
        );
    }
}
