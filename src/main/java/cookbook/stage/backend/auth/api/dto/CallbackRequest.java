package cookbook.stage.backend.auth.api.dto;

public record CallbackRequest(String code, String redirectUri, boolean rememberMe) {
}
