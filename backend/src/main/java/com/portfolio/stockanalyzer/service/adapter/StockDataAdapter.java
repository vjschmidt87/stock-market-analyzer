package com.portfolio.stockanalyzer.service.adapter;

import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.StockSnapshot;

import java.util.List;
import java.util.Map;

public interface StockDataAdapter {

    List<Stock> fetchStocks();

    Map<String, StockSnapshot> fetchLatestPrices(List<String> symbols);

    List<StockSnapshot> fetchHistoricalData(String symbol, int days);
}
