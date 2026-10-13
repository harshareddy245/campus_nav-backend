package com.campusnav.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Detailed profile entity linked 1-to-1 with a User account.
 * Demonstrates Week 4: @OneToOne JPA relationship.
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 30)
    private String rollOrEmployeeId;

    @Column(length = 60)
    private String department;

    @Column(length = 20)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnore
    private User user;

    public UserProfile() {
    }

    public UserProfile(String fullName, String rollOrEmployeeId, String department, String phoneNumber) {
        this.fullName = fullName;
        this.rollOrEmployeeId = rollOrEmployeeId;
        this.department = department;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRollOrEmployeeId() {
        return rollOrEmployeeId;
    }

    public void setRollOrEmployeeId(String rollOrEmployeeId) {
        this.rollOrEmployeeId = rollOrEmployeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
