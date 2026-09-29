package com.portfolio.stockanalyzer.service.analysis;

import com.portfolio.stockanalyzer.entity.AnalysisResult;
import com.portfolio.stockanalyzer.entity.Recommendation;
import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.StockSnapshot;
import com.portfolio.stockanalyzer.repository.AnalysisResultRepository;
import com.portfolio.stockanalyzer.repository.StockSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationEngine {

    private final TechnicalAnalysisService technicalAnalysis;
    private final StockSnapshotRepository snapshotRepository;
    private final AnalysisResultRepository analysisResultRepository;

    @Transactional
    public AnalysisResult analyzeStock(Stock stock) {
        List<StockSnapshot> snapshots = snapshotRepository
                .findByStockIdOrderByDateAsc(stock.getId());

        if (snapshots.size() < 50) {
            log.warn("Insufficient data for analysis of {}: {} snapshots", stock.getSymbol(), snapshots.size());
            return null;
        }

        BigDecimal sma20 = technicalAnalysis.calculateSMA(snapshots, 20);
        BigDecimal sma50 = technicalAnalysis.calculateSMA(snapshots, 50);
        BigDecimal sma200 = technicalAnalysis.calculateSMA(snapshots, 200);
        BigDecimal ema12 = technicalAnalysis.calculateEMA(snapshots, 12);
        BigDecimal ema26 = technicalAnalysis.calculateEMA(snapshots, 26);
        BigDecimal rsi = technicalAnalysis.calculateRSI(snapshots, 14);
        TechnicalAnalysisService.MACDResult macd = technicalAnalysis.calculateMACD(snapshots);
        TechnicalAnalysisService.BollingerBands bollinger = technicalAnalysis.calculateBollingerBands(snapshots, 20);
        BigDecimal volumeChange = snapshots.size() >= 20
                ? technicalAnalysis.calculateVolumeChange(snapshots, 10) : null;

        int score = calculateScore(stock, sma20, sma50, sma200, rsi, macd, bollinger, volumeChange);
        Recommendation recommendation = scoreToRecommendation(score);
        String summary = buildSummary(stock, recommendation, rsi, macd, sma50, sma200);

        AnalysisResult result = AnalysisResult.builder()
                .stock(stock)
                .recommendation(recommendation)
                .overallScore(BigDecimal.valueOf(score))
                .sma20(sma20)
                .sma50(sma50)
                .sma200(sma200)
                .ema12(ema12)
                .ema26(ema26)
                .rsi(rsi)
                .macdLine(macd != null ? macd.macdLine() : null)
                .macdSignal(macd != null ? macd.signalLine() : null)
                .macdHistogram(macd != null ? macd.histogram() : null)
                .bollingerUpper(bollinger != null ? bollinger.upper() : null)
                .bollingerMiddle(bollinger != null ? bollinger.middle() : null)
                .bollingerLower(bollinger != null ? bollinger.lower() : null)
                .volumeChange(volumeChange)
                .summary(summary)
                .analyzedAt(LocalDateTime.now())
                .build();

        return analysisResultRepository.save(result);
    }

    private int calculateScore(Stock stock, BigDecimal sma20, BigDecimal sma50,
                               BigDecimal sma200, BigDecimal rsi,
                               TechnicalAnalysisService.MACDResult macd,
                               TechnicalAnalysisService.BollingerBands bollinger,
                               BigDecimal volumeChange) {
        int score = 50;
        BigDecimal price = stock.getCurrentPrice();

        if (sma20 != null && price.compareTo(sma20) > 0) score += 5;
        else if (sma20 != null) score -= 5;

        if (sma50 != null && price.compareTo(sma50) > 0) score += 8;
        else if (sma50 != null) score -= 8;

        if (sma200 != null && price.compareTo(sma200) > 0) score += 10;
        else if (sma200 != null) score -= 10;

        if (sma50 != null && sma200 != null) {
            if (sma50.compareTo(sma200) > 0) score += 7;
            else score -= 7;
        }

        if (rsi != null) {
            double rsiVal = rsi.doubleValue();
            if (rsiVal < 30) score += 10;
            else if (rsiVal < 40) score += 5;
            else if (rsiVal > 70) score -= 10;
            else if (rsiVal > 60) score -= 5;
        }

        if (macd != null) {
            if (macd.histogram().compareTo(BigDecimal.ZERO) > 0) score += 8;
            else score -= 8;
            if (macd.macdLine().compareTo(macd.signalLine()) > 0) score += 5;
            else score -= 5;
        }

        if (bollinger != null) {
            if (price.compareTo(bollinger.lower()) <= 0) score += 8;
            else if (price.compareTo(bollinger.upper()) >= 0) score -= 8;
        }

        if (volumeChange != null && volumeChange.doubleValue() > 20) score += 3;

        return Math.max(0, Math.min(100, score));
    }

    private Recommendation scoreToRecommendation(int score) {
        if (score >= 80) return Recommendation.STRONG_BUY;
        if (score >= 65) return Recommendation.BUY;
        if (score >= 40) return Recommendation.HOLD;
        if (score >= 25) return Recommendation.SELL;
        return Recommendation.STRONG_SELL;
    }

    private String buildSummary(Stock stock, Recommendation rec, BigDecimal rsi,
                                TechnicalAnalysisService.MACDResult macd,
                                BigDecimal sma50, BigDecimal sma200) {
        List<String> signals = new ArrayList<>();
        BigDecimal price = stock.getCurrentPrice();

        if (sma50 != null && sma200 != null) {
            signals.add(sma50.compareTo(sma200) > 0 ? "Golden Cross (bullish)" : "Death Cross (bearish)");
        }
        if (rsi != null) {
            double rsiVal = rsi.doubleValue();
            if (rsiVal < 30) signals.add("RSI oversold (" + rsi + ")");
            else if (rsiVal > 70) signals.add("RSI overbought (" + rsi + ")");
            else signals.add("RSI neutral (" + rsi + ")");
        }
        if (macd != null) {
            signals.add(macd.histogram().compareTo(BigDecimal.ZERO) > 0
                    ? "MACD bullish momentum" : "MACD bearish momentum");
        }

        return String.format("%s (%s) - %s at $%s. Key signals: %s",
                stock.getSymbol(), stock.getCompanyName(), rec.name(), price,
                String.join("; ", signals));
    }
}
