package com.campusnav.repository;

import com.campusnav.entity.NavigationAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NavigationAuditLogRepository extends JpaRepository<NavigationAuditLog, Long> {
    List<NavigationAuditLog> findTop15ByOrderByTimestampDesc();
}
