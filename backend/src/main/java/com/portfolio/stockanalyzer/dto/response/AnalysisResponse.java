package com.portfolio.stockanalyzer.dto.response;

import com.portfolio.stockanalyzer.entity.AnalysisResult;
import com.portfolio.stockanalyzer.entity.Recommendation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AnalysisResponse(
        Long id,
        String symbol,
        String companyName,
        Recommendation recommendation,
        BigDecimal overallScore,
        BigDecimal sma20,
        BigDecimal sma50,
        BigDecimal sma200,
        BigDecimal ema12,
        BigDecimal ema26,
        BigDecimal rsi,
        BigDecimal macdLine,
        BigDecimal macdSignal,
        BigDecimal macdHistogram,
        BigDecimal bollingerUpper,
        BigDecimal bollingerMiddle,
        BigDecimal bollingerLower,
        BigDecimal volumeChange,
        String summary,
        LocalDateTime analyzedAt
) {
    public static AnalysisResponse from(AnalysisResult result) {
        return new AnalysisResponse(
                result.getId(),
                result.getStock().getSymbol(),
                result.getStock().getCompanyName(),
                result.getRecommendation(),
                result.getOverallScore(),
                result.getSma20(), result.getSma50(), result.getSma200(),
                result.getEma12(), result.getEma26(),
                result.getRsi(),
                result.getMacdLine(), result.getMacdSignal(), result.getMacdHistogram(),
                result.getBollingerUpper(), result.getBollingerMiddle(), result.getBollingerLower(),
                result.getVolumeChange(),
                result.getSummary(),
                result.getAnalyzedAt()
        );
    }
}
