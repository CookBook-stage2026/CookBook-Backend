package cookbook.stage.backend.auth.api;

public record AuthResponse(String token, String email, String displayName) {}
