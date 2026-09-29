package com.portfolio.stockanalyzer.service;

import com.portfolio.stockanalyzer.dto.response.MarketStatsResponse;
import com.portfolio.stockanalyzer.dto.response.StockResponse;
import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public MarketStatsResponse getMarketStats() {
        List<Stock> allStocks = stockRepository.findAll();
        if (allStocks.isEmpty()) {
            return new MarketStatsResponse(0, 0, 0, BigDecimal.ZERO, List.of(), List.of());
        }

        long gainers = 0;
        long losers = 0;
        for (Stock stock : allStocks) {
            if (stock.getPreviousClose() == null) continue;
            int cmp = stock.getCurrentPrice().compareTo(stock.getPreviousClose());
            if (cmp > 0) gainers++;
            else if (cmp < 0) losers++;
        }

        BigDecimal totalMarketCap = allStocks.stream()
                .map(Stock::getMarketCap)
                .filter(mc -> mc != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<StockResponse> topGainers = allStocks.stream()
                .filter(s -> s.getPreviousClose() != null && s.getPreviousClose().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(this::percentChange).reversed())
                .limit(5)
                .map(StockResponse::from)
                .toList();

        List<StockResponse> topLosers = allStocks.stream()
                .filter(s -> s.getPreviousClose() != null && s.getPreviousClose().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(this::percentChange))
                .limit(5)
                .map(StockResponse::from)
                .toList();

        return new MarketStatsResponse(
                allStocks.size(), gainers, losers,
                totalMarketCap, topGainers, topLosers
        );
    }

    private BigDecimal percentChange(Stock stock) {
        return stock.getCurrentPrice().subtract(stock.getPreviousClose())
                .divide(stock.getPreviousClose(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
