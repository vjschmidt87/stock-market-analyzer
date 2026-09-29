package com.portfolio.stockanalyzer.repository;

import com.portfolio.stockanalyzer.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT a FROM Alert a JOIN FETCH a.stock WHERE a.triggered = false")
    List<Alert> findActiveAlerts();

    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId AND a.triggered = true ORDER BY a.triggeredAt DESC")
    List<Alert> findTriggeredByUserId(@Param("userId") Long userId);
}
