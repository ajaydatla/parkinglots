package com.parkinglot.controller;

import com.parkinglot.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Slf4j
public class PublicController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> status = Map.of(
                "status", "UP",
                "service", "Parking Lot Management System",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", status));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInfo() {
        Map<String, Object> info = Map.of(
                "name", "Parking Lot Management System",
                "version", "1.0.0",
                "description", "A comprehensive parking lot management system with OAuth2 authentication",
                "features", java.util.List.of(
                        "Vehicle entry/exit management",
                        "Automatic slot allocation",
                        "Payment processing",
                        "Multi-floor support",
                        "Google OAuth2 authentication",
                        "Admin panel for configuration"
                )
        );
        return ResponseEntity.ok(ApiResponse.success(info));
    }
}
