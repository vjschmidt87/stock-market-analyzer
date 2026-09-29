package com.portfolio.stockanalyzer.service;

import com.portfolio.stockanalyzer.dto.request.CreateWatchlistRequest;
import com.portfolio.stockanalyzer.dto.response.WatchlistResponse;
import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.User;
import com.portfolio.stockanalyzer.entity.Watchlist;
import com.portfolio.stockanalyzer.entity.WatchlistStock;
import com.portfolio.stockanalyzer.exception.DuplicateResourceException;
import com.portfolio.stockanalyzer.exception.ResourceNotFoundException;
import com.portfolio.stockanalyzer.repository.StockRepository;
import com.portfolio.stockanalyzer.repository.WatchlistRepository;
import com.portfolio.stockanalyzer.repository.WatchlistStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistStockRepository watchlistStockRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public List<WatchlistResponse> getUserWatchlists(Long userId) {
        return watchlistRepository.findByUserIdWithItems(userId).stream()
                .map(WatchlistResponse::from)
                .toList();
    }

    @Transactional
    public WatchlistResponse createWatchlist(User user, CreateWatchlistRequest request) {
        Watchlist watchlist = Watchlist.builder()
                .name(request.name())
                .user(user)
                .build();
        watchlistRepository.save(watchlist);
        log.info("Watchlist '{}' created for user {}", request.name(), user.getEmail());
        return WatchlistResponse.from(watchlist);
    }

    @Transactional
    public WatchlistResponse addStockToWatchlist(Long userId, Long watchlistId, String symbol) {
        Watchlist watchlist = watchlistRepository.findByIdAndUserIdWithItems(watchlistId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found: " + symbol));

        if (watchlistStockRepository.existsByWatchlistIdAndStockId(watchlistId, stock.getId())) {
            throw new DuplicateResourceException("Stock already in watchlist");
        }

        int nextPosition = watchlist.getItems().size();
        WatchlistStock item = WatchlistStock.builder()
                .watchlist(watchlist)
                .stock(stock)
                .position(nextPosition)
                .build();
        watchlistStockRepository.save(item);
        watchlist.getItems().add(item);

        log.info("Added {} to watchlist '{}'", symbol, watchlist.getName());
        return WatchlistResponse.from(watchlist);
    }

    @Transactional
    public WatchlistResponse removeStockFromWatchlist(Long userId, Long watchlistId, String symbol) {
        Watchlist watchlist = watchlistRepository.findByIdAndUserIdWithItems(watchlistId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found: " + symbol));

        watchlist.getItems().removeIf(item -> item.getStock().getId().equals(stock.getId()));
        watchlistRepository.save(watchlist);

        log.info("Removed {} from watchlist '{}'", symbol, watchlist.getName());
        return WatchlistResponse.from(watchlist);
    }

    @Transactional
    public void deleteWatchlist(Long userId, Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findByIdAndUserIdWithItems(watchlistId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        watchlistRepository.delete(watchlist);
        log.info("Deleted watchlist '{}'", watchlist.getName());
    }
}
