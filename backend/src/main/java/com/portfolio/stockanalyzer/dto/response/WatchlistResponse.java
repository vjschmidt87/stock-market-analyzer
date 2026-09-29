package com.portfolio.stockanalyzer.dto.response;

import com.portfolio.stockanalyzer.entity.Watchlist;

import java.time.LocalDateTime;
import java.util.List;

public record WatchlistResponse(
        Long id,
        String name,
        List<WatchlistStockResponse> stocks,
        LocalDateTime createdAt
) {
    public static WatchlistResponse from(Watchlist watchlist) {
        List<WatchlistStockResponse> stocks = watchlist.getItems().stream()
                .map(WatchlistStockResponse::from)
                .toList();
        return new WatchlistResponse(watchlist.getId(), watchlist.getName(), stocks, watchlist.getCreatedAt());
    }
}
