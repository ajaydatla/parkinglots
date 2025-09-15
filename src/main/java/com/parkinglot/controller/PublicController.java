package com.parkinglot.controller;

import com.parkinglot.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
@RequestMapping("/")
@Slf4j
public class PublicController {

    @GetMapping("")
    public String getHome(@AuthenticationPrincipal OidcUser oidcUser){
        if (oidcUser != null) {
            // already logged in → go to home
            return "redirect:/home";
        }
        log.info("getting home without user");
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        log.info("oidc user {} authenticated",oidcUser.getFullName());
        String redirectUrl = "home";
        for (GrantedAuthority authority : oidcUser.getAuthorities()) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "redirect:/api/admin/home";
                break;
            } else if (role.equals("ROLE_USER")) {
                redirectUrl = "redirect:/api/parking/home";
                break;
            }
        }
        model.addAttribute("name", oidcUser.getFullName());
        model.addAttribute("email", oidcUser.getEmail());
        return redirectUrl;
    }

    @ResponseBody
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> status = Map.of(
                "status", "UP",
                "service", "Parking Lot Management System",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", status));
    }

    @ResponseBody
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
