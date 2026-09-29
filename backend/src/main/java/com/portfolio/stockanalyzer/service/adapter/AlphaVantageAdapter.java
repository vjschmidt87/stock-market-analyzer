package com.portfolio.stockanalyzer.service.adapter;

import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.StockSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
@ConditionalOnProperty(name = "stock.data.provider", havingValue = "alphavantage")
public class AlphaVantageAdapter implements StockDataAdapter {

    @Value("${stock.alphavantage.api-key:}")
    private String apiKey;

    @Value("${stock.alphavantage.base-url:https://www.alphavantage.co}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Stock> fetchStocks() {
        log.warn("Alpha Vantage does not support bulk stock listing; use mock data for initial seeding");
        return List.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, StockSnapshot> fetchLatestPrices(List<String> symbols) {
        Map<String, StockSnapshot> result = new HashMap<>();
        for (String symbol : symbols) {
            try {
                String url = String.format("%s/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                        baseUrl, symbol, apiKey);
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                if (response == null) continue;

                Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
                if (quote == null || quote.isEmpty()) continue;

                result.put(symbol, StockSnapshot.builder()
                        .date(LocalDate.parse(quote.get("07. latest trading day")))
                        .openPrice(new BigDecimal(quote.get("02. open")))
                        .highPrice(new BigDecimal(quote.get("03. high")))
                        .lowPrice(new BigDecimal(quote.get("04. low")))
                        .closePrice(new BigDecimal(quote.get("05. price")))
                        .volume(Long.parseLong(quote.get("06. volume")))
                        .build());
            } catch (Exception e) {
                log.error("Failed to fetch quote for {}: {}", symbol, e.getMessage());
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StockSnapshot> fetchHistoricalData(String symbol, int days) {
        try {
            String function = days > 100 ? "TIME_SERIES_DAILY" : "TIME_SERIES_DAILY";
            String outputSize = days > 100 ? "full" : "compact";
            String url = String.format("%s/query?function=%s&symbol=%s&outputsize=%s&apikey=%s",
                    baseUrl, function, symbol, outputSize, apiKey);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return List.of();

            Map<String, Map<String, String>> timeSeries =
                    (Map<String, Map<String, String>>) response.get("Time Series (Daily)");
            if (timeSeries == null) return List.of();

            LocalDate cutoff = LocalDate.now().minusDays(days);
            return timeSeries.entrySet().stream()
                    .filter(e -> LocalDate.parse(e.getKey()).isAfter(cutoff))
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> {
                        Map<String, String> data = e.getValue();
                        return StockSnapshot.builder()
                                .date(LocalDate.parse(e.getKey()))
                                .openPrice(new BigDecimal(data.get("1. open")))
                                .highPrice(new BigDecimal(data.get("2. high")))
                                .lowPrice(new BigDecimal(data.get("3. low")))
                                .closePrice(new BigDecimal(data.get("4. close")))
                                .volume(Long.parseLong(data.get("5. volume")))
                                .build();
                    })
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch historical data for {}: {}", symbol, e.getMessage());
            return List.of();
        }
    }
}
