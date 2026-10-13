package com.campusnav.repository;

import com.campusnav.entity.CampusLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampusLocationRepository extends JpaRepository<CampusLocation, Long> {

    Optional<CampusLocation> findByCode(String code);

    List<CampusLocation> findByCategoryIgnoreCase(String category);

    List<CampusLocation> findByBuildingId(Long buildingId);

    @Query("SELECT loc FROM CampusLocation loc WHERE " +
           "LOWER(loc.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(loc.code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(loc.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<CampusLocation> searchLocations(@Param("query") String query);

    @Query("SELECT loc FROM CampusLocation loc WHERE " +
           "(:category IS NULL OR LOWER(loc.category) = LOWER(:category)) AND " +
           "(:query IS NULL OR LOWER(loc.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(loc.code) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<CampusLocation> filterLocationsPaged(
            @Param("category") String category,
            @Param("query") String query,
            Pageable pageable
    );
}
