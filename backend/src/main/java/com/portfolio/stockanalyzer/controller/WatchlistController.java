package com.portfolio.stockanalyzer.controller;

import com.portfolio.stockanalyzer.dto.request.CreateWatchlistRequest;
import com.portfolio.stockanalyzer.dto.response.WatchlistResponse;
import com.portfolio.stockanalyzer.entity.User;
import com.portfolio.stockanalyzer.service.AuthService;
import com.portfolio.stockanalyzer.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final AuthService authService;

    @GetMapping
    public List<WatchlistResponse> getWatchlists(Authentication auth) {
        User user = authService.getCurrentUser(auth.getName());
        return watchlistService.getUserWatchlists(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistResponse createWatchlist(Authentication auth,
                                              @Valid @RequestBody CreateWatchlistRequest request) {
        User user = authService.getCurrentUser(auth.getName());
        return watchlistService.createWatchlist(user, request);
    }

    @PostMapping("/{id}/stocks/{symbol}")
    public WatchlistResponse addStock(Authentication auth,
                                       @PathVariable Long id,
                                       @PathVariable String symbol) {
        User user = authService.getCurrentUser(auth.getName());
        return watchlistService.addStockToWatchlist(user.getId(), id, symbol);
    }

    @DeleteMapping("/{id}/stocks/{symbol}")
    public WatchlistResponse removeStock(Authentication auth,
                                          @PathVariable Long id,
                                          @PathVariable String symbol) {
        User user = authService.getCurrentUser(auth.getName());
        return watchlistService.removeStockFromWatchlist(user.getId(), id, symbol);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWatchlist(Authentication auth, @PathVariable Long id) {
        User user = authService.getCurrentUser(auth.getName());
        watchlistService.deleteWatchlist(user.getId(), id);
    }
}
