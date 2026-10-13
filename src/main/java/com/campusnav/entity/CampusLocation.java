package com.campusnav.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CampusLocation represents a destination or navigational waypoint node on campus.
 * Demonstrates Week 4: @ManyToOne (to Building) and @ManyToMany (to Facility).
 */
@Entity
@Table(name = "campus_locations")
public class CampusLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 40)
    private String category; // CLASSROOM, LAB, LIBRARY, OFFICE, CAFETERIA, AUDITORIUM, RESTROOM, GATE

    private int floor; // 0 = Ground floor, 1 = 1st floor, etc.

    private double mapX;
    private double mapY;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String operatingHours;

    @Column(length = 100)
    private String contactPerson;

    private boolean isWheelchairAccessible = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "location_facilities",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();

    @OneToMany(mappedBy = "fromLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RouteConnection> outgoingConnections = new ArrayList<>();

    @OneToMany(mappedBy = "toLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RouteConnection> incomingConnections = new ArrayList<>();

    public CampusLocation() {
    }

    public CampusLocation(String name, String code, String category, int floor, double mapX, double mapY,
                          String description, String operatingHours, String contactPerson,
                          boolean isWheelchairAccessible, Building building) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.floor = floor;
        this.mapX = mapX;
        this.mapY = mapY;
        this.description = description;
        this.operatingHours = operatingHours;
        this.contactPerson = contactPerson;
        this.isWheelchairAccessible = isWheelchairAccessible;
        this.building = building;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public boolean isWheelchairAccessible() {
        return isWheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        isWheelchairAccessible = wheelchairAccessible;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Set<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(Set<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<RouteConnection> getOutgoingConnections() {
        return outgoingConnections;
    }

    public void setOutgoingConnections(List<RouteConnection> outgoingConnections) {
        this.outgoingConnections = outgoingConnections;
    }

    public List<RouteConnection> getIncomingConnections() {
        return incomingConnections;
    }

    public void setIncomingConnections(List<RouteConnection> incomingConnections) {
        this.incomingConnections = incomingConnections;
    }
}
