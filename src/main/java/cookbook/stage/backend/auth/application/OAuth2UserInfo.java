package cookbook.stage.backend.auth.application;

import java.util.Map;

public record OAuth2UserInfo(
        String provider,
        String providerId,
        String email,
        String name
) {
    public static OAuth2UserInfo from(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> new OAuth2UserInfo(
                    "google",
                    (String) attributes.get("sub"),
                    (String) attributes.get("email"),
                    (String) attributes.get("name")
            );
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
