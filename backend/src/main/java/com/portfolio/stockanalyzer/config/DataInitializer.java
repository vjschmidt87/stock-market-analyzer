package com.portfolio.stockanalyzer.config;

import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.StockSnapshot;
import com.portfolio.stockanalyzer.repository.StockRepository;
import com.portfolio.stockanalyzer.repository.StockSnapshotRepository;
import com.portfolio.stockanalyzer.service.adapter.StockDataAdapter;
import com.portfolio.stockanalyzer.service.analysis.RecommendationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StockRepository stockRepository;
    private final StockSnapshotRepository snapshotRepository;
    private final StockDataAdapter stockDataAdapter;
    private final RecommendationEngine recommendationEngine;

    @Override
    @Transactional
    public void run(String... args) {
        if (stockRepository.count() > 0) {
            log.info("Stocks already initialized, skipping seeding");
            return;
        }

        log.info("Seeding stock data...");
        List<Stock> stocks = stockDataAdapter.fetchStocks();
        for (Stock stock : stocks) {
            stockRepository.save(stock);
            log.info("Saved stock: {} ({})", stock.getSymbol(), stock.getCompanyName());

            List<StockSnapshot> history = stockDataAdapter.fetchHistoricalData(stock.getSymbol(), 730);
            for (StockSnapshot snapshot : history) {
                snapshot.setStock(stock);
                snapshotRepository.save(snapshot);
            }
            log.info("Saved {} snapshots for {}", history.size(), stock.getSymbol());
        }

        log.info("Running initial analysis on all stocks...");
        List<Stock> savedStocks = stockRepository.findAll();
        for (Stock stock : savedStocks) {
            try {
                recommendationEngine.analyzeStock(stock);
                log.info("Analysis complete for {}", stock.getSymbol());
            } catch (Exception e) {
                log.error("Analysis failed for {}: {}", stock.getSymbol(), e.getMessage());
            }
        }
        log.info("Data initialization complete: {} stocks with historical data and analysis", stocks.size());
    }
}
