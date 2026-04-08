package cookbook.stage.backend.auth.api.dto;

public record TokenRefreshResponse(String accessToken, String refreshToken) {
}
