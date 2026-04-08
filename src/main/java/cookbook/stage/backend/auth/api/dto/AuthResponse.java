package cookbook.stage.backend.auth.api.dto;

public record AuthResponse(String accessToken,
                           String refreshToken,
                           String email,
                           String displayName) {
}
