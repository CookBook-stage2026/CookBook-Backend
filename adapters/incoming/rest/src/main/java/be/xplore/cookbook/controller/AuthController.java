package be.xplore.cookbook.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {
    @GetMapping("/auth/status")
    public ResponseEntity<Map<String, Boolean>> status() {
        return ResponseEntity.ok(Map.of("authenticated", true));
    }
}
