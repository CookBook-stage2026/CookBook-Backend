package cookbook.stage.backend.auth.application;

import cookbook.stage.backend.shared.domain.OAuth2Exception;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public record OAuth2UserInfo(
        String provider,
        String providerId,
        String email,
        String name
) {
    public static OAuth2UserInfo from(OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        var principal = token.getPrincipal();
        if (principal == null) {
            throw new OAuth2Exception("User could not be found in the token.");
        }
        var attributes = principal.getAttributes();

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
            case "github" -> {
                String email = (String) attributes.get("email");
                String name = attributes.get("name") != null
                        ? (String) attributes.get("name")
                        : (String) attributes.get("login");
                yield new OAuth2UserInfo(
                        "github",
                        String.valueOf(attributes.get("id")),
                        email,
                        name
                );
            }
            case "microsoft" -> {
                String email = (String) attributes.get("email");
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
