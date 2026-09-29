package com.portfolio.stockanalyzer.service.analysis;

import com.portfolio.stockanalyzer.entity.StockSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class TechnicalAnalysisService {

    private static final int SCALE = 4;

    public BigDecimal calculateSMA(List<StockSnapshot> snapshots, int period) {
        if (snapshots.size() < period) return null;
        List<StockSnapshot> window = snapshots.subList(snapshots.size() - period, snapshots.size());
        BigDecimal sum = window.stream()
                .map(StockSnapshot::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEMA(List<StockSnapshot> snapshots, int period) {
        if (snapshots.size() < period) return null;
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal oneMinusMultiplier = BigDecimal.ONE.subtract(multiplier);

        BigDecimal ema = calculateSMA(snapshots.subList(0, period), period);
        if (ema == null) return null;

        for (int i = period; i < snapshots.size(); i++) {
            BigDecimal close = snapshots.get(i).getClosePrice();
            ema = close.multiply(multiplier).add(ema.multiply(oneMinusMultiplier))
                    .setScale(SCALE, RoundingMode.HALF_UP);
        }
        return ema;
    }

    public BigDecimal calculateRSI(List<StockSnapshot> snapshots, int period) {
        if (snapshots.size() < period + 1) return null;

        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;

        for (int i = 1; i <= period; i++) {
            BigDecimal change = snapshots.get(i).getClosePrice().subtract(snapshots.get(i - 1).getClosePrice());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                avgGain = avgGain.add(change);
            } else {
                avgLoss = avgLoss.add(change.abs());
            }
        }
        avgGain = avgGain.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        avgLoss = avgLoss.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);

        for (int i = period + 1; i < snapshots.size(); i++) {
            BigDecimal change = snapshots.get(i).getClosePrice().subtract(snapshots.get(i - 1).getClosePrice());
            BigDecimal gain = change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO;
            BigDecimal loss = change.compareTo(BigDecimal.ZERO) < 0 ? change.abs() : BigDecimal.ZERO;
            BigDecimal periodMinus1 = BigDecimal.valueOf(period - 1);
            avgGain = avgGain.multiply(periodMinus1).add(gain)
                    .divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
            avgLoss = avgLoss.multiply(periodMinus1).add(loss)
                    .divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        }

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal rs = avgGain.divide(avgLoss, SCALE, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(
                BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), SCALE, RoundingMode.HALF_UP)
        );
    }

    public record MACDResult(BigDecimal macdLine, BigDecimal signalLine, BigDecimal histogram) {}

    public MACDResult calculateMACD(List<StockSnapshot> snapshots) {
        if (snapshots.size() < 35) return null;

        BigDecimal ema12 = calculateEMA(snapshots, 12);
        BigDecimal ema26 = calculateEMA(snapshots, 26);
        if (ema12 == null || ema26 == null) return null;

        BigDecimal macdLine = ema12.subtract(ema26);
        BigDecimal signalLine = macdLine.multiply(BigDecimal.valueOf(0.15))
                .add(macdLine.multiply(BigDecimal.valueOf(0.85)))
                .setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal histogram = macdLine.subtract(signalLine);

        return new MACDResult(macdLine, signalLine, histogram);
    }

    public record BollingerBands(BigDecimal upper, BigDecimal middle, BigDecimal lower) {}

    public BollingerBands calculateBollingerBands(List<StockSnapshot> snapshots, int period) {
        if (snapshots.size() < period) return null;

        BigDecimal sma = calculateSMA(snapshots, period);
        if (sma == null) return null;

        List<StockSnapshot> window = snapshots.subList(snapshots.size() - period, snapshots.size());
        BigDecimal variance = window.stream()
                .map(s -> s.getClosePrice().subtract(sma).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);

        BigDecimal stdDev = BigDecimal.valueOf(Math.sqrt(variance.doubleValue()))
                .setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal twoStdDev = stdDev.multiply(BigDecimal.valueOf(2));

        return new BollingerBands(sma.add(twoStdDev), sma, sma.subtract(twoStdDev));
    }

    public BigDecimal calculateVolumeChange(List<StockSnapshot> snapshots, int period) {
        if (snapshots.size() < period + 1) return null;

        long recentAvg = snapshots.subList(snapshots.size() - period, snapshots.size()).stream()
                .mapToLong(StockSnapshot::getVolume)
                .sum() / period;
        long previousAvg = snapshots.subList(snapshots.size() - (period * 2), snapshots.size() - period).stream()
                .mapToLong(StockSnapshot::getVolume)
                .sum() / period;

        if (previousAvg == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf((double)(recentAvg - previousAvg) / previousAvg * 100)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
