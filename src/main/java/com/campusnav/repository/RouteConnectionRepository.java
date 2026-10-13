package com.campusnav.repository;

import com.campusnav.entity.RouteConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteConnectionRepository extends JpaRepository<RouteConnection, Long> {
    List<RouteConnection> findByFromLocationId(Long fromLocationId);
    List<RouteConnection> findByToLocationId(Long toLocationId);
}
