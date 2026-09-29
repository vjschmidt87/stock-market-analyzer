package com.portfolio.stockanalyzer.controller;

import com.portfolio.stockanalyzer.dto.response.AnalysisResponse;
import com.portfolio.stockanalyzer.entity.AnalysisResult;
import com.portfolio.stockanalyzer.entity.Recommendation;
import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.exception.ResourceNotFoundException;
import com.portfolio.stockanalyzer.repository.AnalysisResultRepository;
import com.portfolio.stockanalyzer.repository.StockRepository;
import com.portfolio.stockanalyzer.service.analysis.RecommendationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisResultRepository analysisResultRepository;
    private final StockRepository stockRepository;
    private final RecommendationEngine recommendationEngine;

    @GetMapping("/{symbol}")
    public AnalysisResponse getAnalysis(@PathVariable String symbol) {
        return analysisResultRepository.findLatestBySymbol(symbol.toUpperCase())
                .map(AnalysisResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("No analysis found for: " + symbol));
    }

    @GetMapping("/recommendation/{recommendation}")
    public List<AnalysisResponse> getByRecommendation(@PathVariable String recommendation) {
        Recommendation rec = Recommendation.valueOf(recommendation.toUpperCase());
        return analysisResultRepository.findLatestByRecommendation(rec, PageRequest.of(0, 100))
                .stream()
                .map(AnalysisResponse::from)
                .toList();
    }

    @PostMapping("/{symbol}/refresh")
    public AnalysisResponse refreshAnalysis(@PathVariable String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found: " + symbol));
        AnalysisResult result = recommendationEngine.analyzeStock(stock);
        if (result == null) {
            throw new ResourceNotFoundException("Insufficient data to analyze: " + symbol);
        }
        return AnalysisResponse.from(result);
    }
}
