package com.campusnav.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Amenity / Facility entity (e.g., Elevator, Drinking Water, Restroom, Ramp, ATM).
 * Demonstrates Week 4: @ManyToMany JPA relationship with CampusLocation.
 */
@Entity
@Table(name = "facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 50)
    private String category; // RESTROOM, WATER, ACCESSIBILITY, SERVICES, FOOD

    @Column(length = 50)
    private String iconName;

    @ManyToMany(mappedBy = "facilities", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CampusLocation> locations = new HashSet<>();

    public Facility() {
    }

    public Facility(String name, String category, String iconName) {
        this.name = name;
        this.category = category;
        this.iconName = iconName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Set<CampusLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<CampusLocation> locations) {
        this.locations = locations;
    }
}
