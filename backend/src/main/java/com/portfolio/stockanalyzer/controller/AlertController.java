package com.portfolio.stockanalyzer.controller;

import com.portfolio.stockanalyzer.dto.request.CreateAlertRequest;
import com.portfolio.stockanalyzer.dto.response.AlertResponse;
import com.portfolio.stockanalyzer.entity.User;
import com.portfolio.stockanalyzer.service.AlertService;
import com.portfolio.stockanalyzer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final AuthService authService;

    @GetMapping
    public List<AlertResponse> getAlerts(Authentication auth) {
        User user = authService.getCurrentUser(auth.getName());
        return alertService.getUserAlerts(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertResponse createAlert(Authentication auth,
                                      @Valid @RequestBody CreateAlertRequest request) {
        User user = authService.getCurrentUser(auth.getName());
        return alertService.createAlert(user, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlert(Authentication auth, @PathVariable Long id) {
        User user = authService.getCurrentUser(auth.getName());
        alertService.deleteAlert(user.getId(), id);
    }
}
