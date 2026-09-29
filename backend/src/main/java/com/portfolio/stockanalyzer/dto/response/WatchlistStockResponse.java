package com.portfolio.stockanalyzer.dto.response;

import com.portfolio.stockanalyzer.entity.WatchlistStock;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record WatchlistStockResponse(
        Long id,
        String symbol,
        String companyName,
        BigDecimal currentPrice,
        BigDecimal changePercent,
        Integer position
) {
    public static WatchlistStockResponse from(WatchlistStock ws) {
        var stock = ws.getStock();
        BigDecimal changePct = null;
        if (stock.getCurrentPrice() != null && stock.getPreviousClose() != null
                && stock.getPreviousClose().compareTo(BigDecimal.ZERO) != 0) {
            changePct = stock.getCurrentPrice().subtract(stock.getPreviousClose())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(stock.getPreviousClose(), 2, RoundingMode.HALF_UP);
        }
        return new WatchlistStockResponse(
                ws.getId(), stock.getSymbol(), stock.getCompanyName(),
                stock.getCurrentPrice(), changePct, ws.getPosition()
        );
    }
}
