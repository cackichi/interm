package org.example.controllers;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CircuitController {
    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;
    @GetMapping("/status/{name}")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerStatus(@PathVariable String name) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);

        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("state", circuitBreaker.getState().name());

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("failureRate", circuitBreaker.getMetrics().getFailureRate() + "%");
        metrics.put("slowCallRate", circuitBreaker.getMetrics().getSlowCallRate() + "%");
        metrics.put("bufferedCalls", circuitBreaker.getMetrics().getNumberOfBufferedCalls());
        metrics.put("failedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
        metrics.put("notPermittedCalls", circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());

        response.put("metrics", metrics);

        return ResponseEntity.ok(response);
    }
}
