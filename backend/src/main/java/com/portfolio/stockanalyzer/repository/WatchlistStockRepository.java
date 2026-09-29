package com.portfolio.stockanalyzer.repository;

import com.portfolio.stockanalyzer.entity.WatchlistStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistStockRepository extends JpaRepository<WatchlistStock, Long> {
    boolean existsByWatchlistIdAndStockId(Long watchlistId, Long stockId);
}
