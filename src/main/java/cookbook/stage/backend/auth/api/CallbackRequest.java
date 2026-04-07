package cookbook.stage.backend.auth.api;

public record CallbackRequest(String code, String redirectUri) {}
