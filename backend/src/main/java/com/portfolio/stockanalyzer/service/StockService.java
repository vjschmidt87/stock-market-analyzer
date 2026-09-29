package com.portfolio.stockanalyzer.service;

import com.portfolio.stockanalyzer.dto.response.SnapshotResponse;
import com.portfolio.stockanalyzer.dto.response.StockResponse;
import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.exception.ResourceNotFoundException;
import com.portfolio.stockanalyzer.repository.StockRepository;
import com.portfolio.stockanalyzer.repository.StockSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockSnapshotRepository snapshotRepository;

    @Transactional(readOnly = true)
    public Page<StockResponse> getAllStocks(Pageable pageable) {
        return stockRepository.findAll(pageable).map(StockResponse::from);
    }

    @Transactional(readOnly = true)
    public StockResponse getStockBySymbol(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found: " + symbol));
        return StockResponse.from(stock);
    }

    @Transactional(readOnly = true)
    public Page<StockResponse> searchStocks(String query, Pageable pageable) {
        return stockRepository.search(query, pageable).map(StockResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<StockResponse> getStocksBySector(String sector, Pageable pageable) {
        return stockRepository.findBySector(sector, pageable).map(StockResponse::from);
    }

    @Transactional(readOnly = true)
    public List<String> getAllSectors() {
        return stockRepository.findAllSectors();
    }

    @Transactional(readOnly = true)
    public List<SnapshotResponse> getStockHistory(String symbol, String period) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found: " + symbol));

        LocalDate startDate = switch (period) {
            case "1W" -> LocalDate.now().minusWeeks(1);
            case "1M" -> LocalDate.now().minusMonths(1);
            case "3M" -> LocalDate.now().minusMonths(3);
            case "6M" -> LocalDate.now().minusMonths(6);
            case "1Y" -> LocalDate.now().minusYears(1);
            case "5Y" -> LocalDate.now().minusYears(5);
            default -> LocalDate.now().minusMonths(3);
        };

        return snapshotRepository.findByStockIdAndDateAfter(stock.getId(), startDate)
                .stream()
                .map(SnapshotResponse::from)
                .toList();
    }
}
