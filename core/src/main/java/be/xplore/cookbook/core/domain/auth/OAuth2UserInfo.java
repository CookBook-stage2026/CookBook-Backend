package be.xplore.cookbook.core.domain.auth;

public record OAuth2UserInfo(
        String provider,
        String providerId,
        String email,
        String name
) {
}
