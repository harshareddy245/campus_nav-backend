package com.campusnav.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Building entity representing a physical infrastructure block on campus.
 * Demonstrates Week 4: @OneToMany relationship with CampusLocation.
 */
@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 255)
    private String description;

    private int totalFloors;

    // Relative coordinates for the 2D interactive campus map
    private double mapX;
    private double mapY;
    private double width;
    private double height;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    private List<CampusLocation> locations = new ArrayList<>();

    public Building() {
    }

    public Building(String name, String code, String description, int totalFloors, double mapX, double mapY, double width, double height) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.totalFloors = totalFloors;
        this.mapX = mapX;
        this.mapY = mapY;
        this.width = width;
        this.height = height;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(int totalFloors) {
        this.totalFloors = totalFloors;
    }

    public double getMapX() {
        return mapX;
    }

    public void setMapX(double mapX) {
        this.mapX = mapX;
    }

    public double getMapY() {
        return mapY;
    }

    public void setMapY(double mapY) {
        this.mapY = mapY;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<CampusLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<CampusLocation> locations) {
        this.locations = locations;
    }
}
