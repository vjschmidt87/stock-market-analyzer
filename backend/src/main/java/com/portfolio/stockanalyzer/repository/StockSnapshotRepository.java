package com.portfolio.stockanalyzer.repository;

import com.portfolio.stockanalyzer.entity.StockSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockSnapshotRepository extends JpaRepository<StockSnapshot, Long> {

    @Query("SELECT ss FROM StockSnapshot ss WHERE ss.stock.id = :stockId AND ss.date >= :startDate ORDER BY ss.date ASC")
    List<StockSnapshot> findByStockIdAndDateAfter(@Param("stockId") Long stockId, @Param("startDate") LocalDate startDate);

    @Query("SELECT ss FROM StockSnapshot ss WHERE ss.stock.id = :stockId ORDER BY ss.date DESC")
    List<StockSnapshot> findByStockIdOrderByDateDesc(@Param("stockId") Long stockId);

    @Query("SELECT ss FROM StockSnapshot ss WHERE ss.stock.id = :stockId ORDER BY ss.date ASC")
    List<StockSnapshot> findByStockIdOrderByDateAsc(@Param("stockId") Long stockId);

    boolean existsByStockIdAndDate(Long stockId, LocalDate date);
}
