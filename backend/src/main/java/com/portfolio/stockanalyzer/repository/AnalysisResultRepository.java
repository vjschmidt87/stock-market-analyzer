package com.portfolio.stockanalyzer.repository;

import com.portfolio.stockanalyzer.entity.AnalysisResult;
import com.portfolio.stockanalyzer.entity.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    @Query("SELECT ar FROM AnalysisResult ar JOIN FETCH ar.stock WHERE ar.stock.id = :stockId ORDER BY ar.analyzedAt DESC LIMIT 1")
    Optional<AnalysisResult> findLatestByStockId(@Param("stockId") Long stockId);

    @Query("SELECT ar FROM AnalysisResult ar JOIN FETCH ar.stock WHERE ar.stock.symbol = :symbol ORDER BY ar.analyzedAt DESC LIMIT 1")
    Optional<AnalysisResult> findLatestBySymbol(@Param("symbol") String symbol);

    @Query(value = "SELECT ar FROM AnalysisResult ar JOIN FETCH ar.stock WHERE ar.id IN " +
            "(SELECT MAX(a.id) FROM AnalysisResult a GROUP BY a.stock.id) " +
            "AND ar.recommendation = :recommendation",
            countQuery = "SELECT COUNT(ar) FROM AnalysisResult ar WHERE ar.id IN " +
            "(SELECT MAX(a.id) FROM AnalysisResult a GROUP BY a.stock.id) " +
            "AND ar.recommendation = :recommendation")
    Page<AnalysisResult> findLatestByRecommendation(@Param("recommendation") Recommendation recommendation, Pageable pageable);
}
