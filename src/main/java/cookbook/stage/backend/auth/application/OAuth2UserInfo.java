package cookbook.stage.backend.auth.application;

import java.util.Map;

public record OAuth2UserInfo(
        String provider,
        String providerId,
        String email,
        String name
) {
    public static OAuth2UserInfo from(String provider, Map<String, Object> attributes) {
        String emailWord = "email";
        return switch (provider.toLowerCase()) {
            case "google" -> new OAuth2UserInfo(
                    "google",
                    (String) attributes.get("sub"),
                    (String) attributes.get(emailWord),
                    (String) attributes.get("name")
            );
            case "github" -> {
                String id = String.valueOf(attributes.get("id"));
                String email = (String) attributes.get(emailWord);

                if (email == null || email.isBlank()) {
                    email = id + "@github.local";
                }

                String name = attributes.get("name") != null
                        ? (String) attributes.get("name")
                        : (String) attributes.get("login");

                yield new OAuth2UserInfo("github", id, email, name);
            }
            case "microsoft" -> new OAuth2UserInfo(
                    "microsoft",
                    (String) attributes.get("sub"),
                    (String) attributes.get(emailWord),
                    (String) attributes.get("name")
            );
            case "github" -> new OAuth2UserInfo(
                    "github",
                    String.valueOf(attributes.get("id")),
                    (String) attributes.get("email"),
                    attributes.get("name") != null
                            ? (String) attributes.get("name")
                            : (String) attributes.get("login")
            );
            case "microsoft" -> {
                String email = (String) attributes.get("email");
                if (email == null) {
                    email = (String) attributes.get("name");
                }
                yield new OAuth2UserInfo(
                        "microsoft",
                        (String) attributes.get("sub"),
                        email,
                        (String) attributes.get("name")
                );
            }
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
