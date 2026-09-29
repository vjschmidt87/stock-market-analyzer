package com.portfolio.stockanalyzer.controller;

import com.portfolio.stockanalyzer.dto.response.SnapshotResponse;
import com.portfolio.stockanalyzer.dto.response.StockResponse;
import com.portfolio.stockanalyzer.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public Page<StockResponse> getAllStocks(Pageable pageable) {
        return stockService.getAllStocks(pageable);
    }

    @GetMapping("/{symbol}")
    public StockResponse getStock(@PathVariable String symbol) {
        return stockService.getStockBySymbol(symbol);
    }

    @GetMapping("/search")
    public Page<StockResponse> searchStocks(@RequestParam String q, Pageable pageable) {
        return stockService.searchStocks(q, pageable);
    }

    @GetMapping("/sectors")
    public List<String> getSectors() {
        return stockService.getAllSectors();
    }

    @GetMapping("/sector/{sector}")
    public Page<StockResponse> getStocksBySector(@PathVariable String sector, Pageable pageable) {
        return stockService.getStocksBySector(sector, pageable);
    }

    @GetMapping("/{symbol}/history")
    public List<SnapshotResponse> getHistory(@PathVariable String symbol,
                                              @RequestParam(defaultValue = "3M") String period) {
        return stockService.getStockHistory(symbol, period);
    }
}
