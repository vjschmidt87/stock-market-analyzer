package com.portfolio.stockanalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Recommendation recommendation;

    private BigDecimal overallScore;

    private BigDecimal sma20;
    private BigDecimal sma50;
    private BigDecimal sma200;
    private BigDecimal ema12;
    private BigDecimal ema26;
    private BigDecimal rsi;
    private BigDecimal macdLine;
    private BigDecimal macdSignal;
    private BigDecimal macdHistogram;
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerMiddle;
    private BigDecimal bollingerLower;
    private BigDecimal volumeChange;

    private String summary;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime analyzedAt = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisResult that = (AnalysisResult) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
