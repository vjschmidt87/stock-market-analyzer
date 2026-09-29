package com.portfolio.stockanalyzer.controller;

import com.portfolio.stockanalyzer.dto.response.MarketStatsResponse;
import com.portfolio.stockanalyzer.service.MarketService;
import com.portfolio.stockanalyzer.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;
    private final SseService sseService;

    @GetMapping("/stats")
    public MarketStatsResponse getMarketStats() {
        return marketService.getMarketStats();
    }

    @GetMapping("/stream")
    public SseEmitter streamPrices() {
        return sseService.createEmitter();
    }
}
