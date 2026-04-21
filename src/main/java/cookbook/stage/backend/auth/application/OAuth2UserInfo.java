package cookbook.stage.backend.auth.application;

public record OAuth2UserInfo(
        String provider,
        String providerId,
        String email,
        String name
) {
}
