package com.portfolio.stockanalyzer.service;

import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.StockSnapshot;
import com.portfolio.stockanalyzer.repository.StockRepository;
import com.portfolio.stockanalyzer.repository.StockSnapshotRepository;
import com.portfolio.stockanalyzer.service.adapter.StockDataAdapter;
import com.portfolio.stockanalyzer.service.analysis.RecommendationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockSyncService {

    private final StockRepository stockRepository;
    private final StockSnapshotRepository snapshotRepository;
    private final StockDataAdapter stockDataAdapter;
    private final RecommendationEngine recommendationEngine;
    private final AlertService alertService;
    private final SseService sseService;

    @Scheduled(
            initialDelayString = "${stock.sync.initial-delay:30000}",
            fixedRateString = "${stock.sync.interval:3600000}"
    )
    @Transactional
    public void syncPrices() {
        log.info("Starting stock price sync...");
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            log.info("No stocks to sync");
            return;
        }

        List<String> symbols = stocks.stream().map(Stock::getSymbol).toList();
        Map<String, StockSnapshot> latestPrices = stockDataAdapter.fetchLatestPrices(symbols);

        int updated = 0;
        for (Stock stock : stocks) {
            StockSnapshot snapshot = latestPrices.get(stock.getSymbol());
            if (snapshot == null) continue;

            stock.setPreviousClose(stock.getCurrentPrice());
            stock.setCurrentPrice(snapshot.getClosePrice());
            stock.setDayHigh(snapshot.getHighPrice());
            stock.setDayLow(snapshot.getLowPrice());
            stock.setVolume(snapshot.getVolume());
            stockRepository.save(stock);

            if (!snapshotRepository.existsByStockIdAndDate(stock.getId(), snapshot.getDate())) {
                snapshot.setStock(stock);
                snapshotRepository.save(snapshot);
            }
            updated++;
        }
        log.info("Synced prices for {}/{} stocks", updated, stocks.size());

        sseService.broadcast("price-update", Map.of("updated", updated, "total", stocks.size()));
        alertService.checkAndTriggerAlerts();
    }

    @Scheduled(cron = "0 0 2 * * SAT")
    @Transactional
    public void weeklyAnalysis() {
        log.info("Starting weekly stock analysis...");
        List<Stock> stocks = stockRepository.findAll();
        int analyzed = 0;
        for (Stock stock : stocks) {
            try {
                recommendationEngine.analyzeStock(stock);
                analyzed++;
            } catch (Exception e) {
                log.error("Failed to analyze {}: {}", stock.getSymbol(), e.getMessage());
            }
        }
        log.info("Weekly analysis complete: {}/{} stocks analyzed", analyzed, stocks.size());
    }
}
