package com.portfolio.stockanalyzer.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record MarketStatsResponse(
        long totalStocks,
        long gainers,
        long losers,
        BigDecimal totalMarketCap,
        List<StockResponse> topGainers,
        List<StockResponse> topLosers
) {}
