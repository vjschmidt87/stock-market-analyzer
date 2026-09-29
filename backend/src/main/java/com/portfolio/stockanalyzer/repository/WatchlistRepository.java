package com.portfolio.stockanalyzer.repository;

import com.portfolio.stockanalyzer.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    @Query("SELECT w FROM Watchlist w LEFT JOIN FETCH w.items wi LEFT JOIN FETCH wi.stock WHERE w.user.id = :userId")
    List<Watchlist> findByUserIdWithItems(@Param("userId") Long userId);

    @Query("SELECT w FROM Watchlist w LEFT JOIN FETCH w.items wi LEFT JOIN FETCH wi.stock WHERE w.id = :id AND w.user.id = :userId")
    Optional<Watchlist> findByIdAndUserIdWithItems(@Param("id") Long id, @Param("userId") Long userId);
}
