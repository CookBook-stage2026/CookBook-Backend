package cookbook.stage.backend.auth.api;

import cookbook.stage.backend.auth.api.dto.AuthResponse;
import cookbook.stage.backend.auth.api.dto.CallbackRequest;
import cookbook.stage.backend.auth.api.dto.TokenRefreshRequest;
import cookbook.stage.backend.auth.api.dto.TokenRefreshResponse;
import cookbook.stage.backend.auth.application.OAuth2AuthService;
import cookbook.stage.backend.auth.application.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.management.ServiceNotFoundException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OAuth2AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(OAuth2AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/{provider}/url")
    public ResponseEntity<Map<String, String>> getAuthUrl(
            @PathVariable String provider,
            @RequestParam String redirectUri) {
        String url = authService.buildAuthorizationUrl(provider, redirectUri);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/{provider}/callback")
    public ResponseEntity<AuthResponse> handleCallback(
            @PathVariable String provider,
            @RequestBody CallbackRequest request) throws ServiceNotFoundException {
        return ResponseEntity.ok(authService.handleCallback(
                provider, request.code(), request.redirectUri(), request.rememberMe()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(refreshTokenService.refreshAccessToken(request.refreshToken()));
    }
}
