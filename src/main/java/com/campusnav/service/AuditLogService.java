package com.campusnav.service;

import com.campusnav.entity.NavigationAuditLog;
import com.campusnav.repository.NavigationAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Asynchronous service for logging navigation searches and calculating metrics.
 * Demonstrates Week 9-10: Async Processing (@Async).
 */
@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);
    private final NavigationAuditLogRepository auditLogRepository;

    public AuditLogService(NavigationAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async("taskExecutor")
    public void recordNavigationSearch(String sourceName, String destinationName,
                                       double distanceMeters, long executionTimeMs, String userAgent) {
        try {
            NavigationAuditLog auditLog = new NavigationAuditLog(
                    sourceName, destinationName, distanceMeters, executionTimeMs, userAgent
            );
            auditLogRepository.save(auditLog);
            log.debug("Async logged route calculation from '{}' to '{}' in {}ms",
                    sourceName, destinationName, executionTimeMs);
        } catch (Exception e) {
            log.error("Failed to asynchronously save audit log: {}", e.getMessage());
        }
    }

    public List<NavigationAuditLog> getRecentSearches() {
        return auditLogRepository.findTop15ByOrderByTimestampDesc();
    }
}
