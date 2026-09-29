package com.portfolio.stockanalyzer.dto.response;

import com.portfolio.stockanalyzer.entity.Stock;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record StockResponse(
        Long id,
        String symbol,
        String companyName,
        String sector,
        String industry,
        String exchange,
        BigDecimal currentPrice,
        BigDecimal previousClose,
        BigDecimal changeAmount,
        BigDecimal changePercent,
        BigDecimal dayHigh,
        BigDecimal dayLow,
        BigDecimal weekHigh52,
        BigDecimal weekLow52,
        Long volume,
        BigDecimal marketCap,
        BigDecimal peRatio,
        BigDecimal dividendYield
) {
    public static StockResponse from(Stock stock) {
        BigDecimal change = null;
        BigDecimal changePct = null;
        if (stock.getCurrentPrice() != null && stock.getPreviousClose() != null
                && stock.getPreviousClose().compareTo(BigDecimal.ZERO) != 0) {
            change = stock.getCurrentPrice().subtract(stock.getPreviousClose());
            changePct = change.multiply(BigDecimal.valueOf(100))
                    .divide(stock.getPreviousClose(), 2, RoundingMode.HALF_UP);
        }
        return new StockResponse(
                stock.getId(), stock.getSymbol(), stock.getCompanyName(),
                stock.getSector(), stock.getIndustry(), stock.getExchange(),
                stock.getCurrentPrice(), stock.getPreviousClose(),
                change, changePct,
                stock.getDayHigh(), stock.getDayLow(),
                stock.getWeekHigh52(), stock.getWeekLow52(),
                stock.getVolume(), stock.getMarketCap(),
                stock.getPeRatio(), stock.getDividendYield()
        );
    }
}
