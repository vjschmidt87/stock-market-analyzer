package com.portfolio.stockanalyzer.service.adapter;

import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.StockSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@ConditionalOnProperty(name = "stock.data.provider", havingValue = "mock", matchIfMissing = true)
public class MockStockAdapter implements StockDataAdapter {

    private static final List<StockDefinition> STOCKS = List.of(
            new StockDefinition("AAPL", "Apple Inc.", "Technology", "Consumer Electronics", "NASDAQ", 178.50, 2_900_000_000_000L),
            new StockDefinition("MSFT", "Microsoft Corporation", "Technology", "Software", "NASDAQ", 378.90, 2_800_000_000_000L),
            new StockDefinition("GOOGL", "Alphabet Inc.", "Technology", "Internet Services", "NASDAQ", 141.80, 1_780_000_000_000L),
            new StockDefinition("AMZN", "Amazon.com Inc.", "Consumer Cyclical", "E-Commerce", "NASDAQ", 178.25, 1_850_000_000_000L),
            new StockDefinition("NVDA", "NVIDIA Corporation", "Technology", "Semiconductors", "NASDAQ", 875.30, 2_150_000_000_000L),
            new StockDefinition("META", "Meta Platforms Inc.", "Technology", "Social Media", "NASDAQ", 505.75, 1_300_000_000_000L),
            new StockDefinition("TSLA", "Tesla Inc.", "Consumer Cyclical", "Auto Manufacturers", "NASDAQ", 175.20, 558_000_000_000L),
            new StockDefinition("JPM", "JPMorgan Chase & Co.", "Financial Services", "Banks", "NYSE", 198.45, 570_000_000_000L),
            new StockDefinition("V", "Visa Inc.", "Financial Services", "Credit Services", "NYSE", 280.60, 580_000_000_000L),
            new StockDefinition("JNJ", "Johnson & Johnson", "Healthcare", "Drug Manufacturers", "NYSE", 156.30, 376_000_000_000L),
            new StockDefinition("WMT", "Walmart Inc.", "Consumer Defensive", "Discount Stores", "NYSE", 165.80, 445_000_000_000L),
            new StockDefinition("PG", "Procter & Gamble Co.", "Consumer Defensive", "Household Products", "NYSE", 162.45, 383_000_000_000L),
            new StockDefinition("XOM", "Exxon Mobil Corporation", "Energy", "Oil & Gas", "NYSE", 104.75, 418_000_000_000L),
            new StockDefinition("UNH", "UnitedHealth Group Inc.", "Healthcare", "Health Care Plans", "NYSE", 528.90, 490_000_000_000L),
            new StockDefinition("HD", "The Home Depot Inc.", "Consumer Cyclical", "Home Improvement", "NYSE", 345.20, 344_000_000_000L),
            new StockDefinition("BAC", "Bank of America Corp.", "Financial Services", "Banks", "NYSE", 34.85, 277_000_000_000L),
            new StockDefinition("DIS", "The Walt Disney Co.", "Communication Services", "Entertainment", "NYSE", 112.40, 205_000_000_000L),
            new StockDefinition("NFLX", "Netflix Inc.", "Communication Services", "Entertainment", "NASDAQ", 628.50, 272_000_000_000L),
            new StockDefinition("ADBE", "Adobe Inc.", "Technology", "Software", "NASDAQ", 575.30, 256_000_000_000L),
            new StockDefinition("CRM", "Salesforce Inc.", "Technology", "Software", "NYSE", 270.15, 262_000_000_000L)
    );

    @Override
    public List<Stock> fetchStocks() {
        return STOCKS.stream().map(def -> Stock.builder()
                .symbol(def.symbol)
                .companyName(def.name)
                .sector(def.sector)
                .industry(def.industry)
                .exchange(def.exchange)
                .description("Mock data for " + def.name)
                .currentPrice(BigDecimal.valueOf(def.basePrice))
                .previousClose(BigDecimal.valueOf(def.basePrice * randomFactor(0.98, 1.02)))
                .dayHigh(BigDecimal.valueOf(def.basePrice * randomFactor(1.00, 1.03)))
                .dayLow(BigDecimal.valueOf(def.basePrice * randomFactor(0.97, 1.00)))
                .weekHigh52(BigDecimal.valueOf(def.basePrice * randomFactor(1.10, 1.40)))
                .weekLow52(BigDecimal.valueOf(def.basePrice * randomFactor(0.60, 0.90)))
                .volume(ThreadLocalRandom.current().nextLong(5_000_000, 80_000_000))
                .avgVolume(ThreadLocalRandom.current().nextLong(10_000_000, 50_000_000))
                .marketCap(BigDecimal.valueOf(def.marketCap))
                .peRatio(BigDecimal.valueOf(randomFactor(12, 45)))
                .dividendYield(BigDecimal.valueOf(randomFactor(0, 3.5)))
                .build()
        ).toList();
    }

    @Override
    public Map<String, StockSnapshot> fetchLatestPrices(List<String> symbols) {
        Map<String, StockSnapshot> result = new HashMap<>();
        for (String symbol : symbols) {
            STOCKS.stream()
                    .filter(s -> s.symbol.equals(symbol))
                    .findFirst()
                    .ifPresent(def -> {
                        double price = def.basePrice * randomFactor(0.97, 1.03);
                        result.put(symbol, StockSnapshot.builder()
                                .date(LocalDate.now())
                                .openPrice(BigDecimal.valueOf(price * randomFactor(0.99, 1.01)))
                                .highPrice(BigDecimal.valueOf(price * randomFactor(1.00, 1.02)))
                                .lowPrice(BigDecimal.valueOf(price * randomFactor(0.98, 1.00)))
                                .closePrice(BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP))
                                .volume(ThreadLocalRandom.current().nextLong(5_000_000, 80_000_000))
                                .build());
                    });
        }
        return result;
    }

    @Override
    public List<StockSnapshot> fetchHistoricalData(String symbol, int days) {
        Optional<StockDefinition> stockDef = STOCKS.stream()
                .filter(s -> s.symbol.equals(symbol))
                .findFirst();

        if (stockDef.isEmpty()) return List.of();

        double basePrice = stockDef.get().basePrice;
        List<StockSnapshot> snapshots = new ArrayList<>();
        double currentPrice = basePrice * randomFactor(0.70, 0.85);

        for (int i = days; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            if (date.getDayOfWeek().getValue() > 5) continue;

            double dailyChange = randomFactor(0.97, 1.03);
            currentPrice *= dailyChange;

            double trendBias = (basePrice - currentPrice) / basePrice * 0.01;
            currentPrice *= (1 + trendBias);

            double open = currentPrice * randomFactor(0.99, 1.01);
            double high = Math.max(open, currentPrice) * randomFactor(1.00, 1.02);
            double low = Math.min(open, currentPrice) * randomFactor(0.98, 1.00);

            snapshots.add(StockSnapshot.builder()
                    .date(date)
                    .openPrice(BigDecimal.valueOf(open).setScale(2, RoundingMode.HALF_UP))
                    .highPrice(BigDecimal.valueOf(high).setScale(2, RoundingMode.HALF_UP))
                    .lowPrice(BigDecimal.valueOf(low).setScale(2, RoundingMode.HALF_UP))
                    .closePrice(BigDecimal.valueOf(currentPrice).setScale(2, RoundingMode.HALF_UP))
                    .volume(ThreadLocalRandom.current().nextLong(5_000_000, 80_000_000))
                    .build());
        }
        return snapshots;
    }

    private double randomFactor(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private record StockDefinition(String symbol, String name, String sector,
                                    String industry, String exchange,
                                    double basePrice, long marketCap) {}
}
