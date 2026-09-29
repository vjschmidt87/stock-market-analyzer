package com.portfolio.stockanalyzer.repository;

import com.portfolio.stockanalyzer.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);

    @Query("SELECT s FROM Stock s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Stock> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.sector = :sector")
    Page<Stock> findBySector(@Param("sector") String sector, Pageable pageable);

    @Query("SELECT DISTINCT s.sector FROM Stock s WHERE s.sector IS NOT NULL ORDER BY s.sector")
    java.util.List<String> findAllSectors();
}
