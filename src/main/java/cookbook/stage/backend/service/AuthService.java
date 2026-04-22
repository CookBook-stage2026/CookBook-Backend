package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.auth.OAuth2UserInfo;
import cookbook.stage.backend.domain.exception.OAuth2Exception;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    public OAuth2UserInfo getUserFromToken(OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        var principal = token.getPrincipal();
        if (principal == null) {
            throw new OAuth2Exception("User could not be found in the token.");
        }
        var attributes = principal.getAttributes();

        String emailWord = "email";
        return switch (provider.toLowerCase()) {
            case "google" -> getOAuth2UserInfoGoogle(attributes, emailWord);
            case "github" -> getOAuth2UserInfoGithub(attributes, emailWord);
            case "microsoft" -> getOAuth2UserInfoMicrosoft(attributes, emailWord);
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    private OAuth2UserInfo getOAuth2UserInfoMicrosoft(Map<String, Object> attributes, String emailWord) {
        return new OAuth2UserInfo(
                "microsoft",
                (String) attributes.get("sub"),
                (String) attributes.get(emailWord),
                (String) attributes.get("name")
        );
    }

    private OAuth2UserInfo getOAuth2UserInfoGithub(Map<String, Object> attributes, String emailWord) {
        String id = String.valueOf(attributes.get("id"));
        String email = (String) attributes.get(emailWord);

        if (email == null || email.isBlank()) {
            email = id + "@github.local";
        }

        String name = attributes.get("name") != null
                ? (String) attributes.get("name")
                : (String) attributes.get("login");

        return new OAuth2UserInfo("github", id, email, name);
    }

    private OAuth2UserInfo getOAuth2UserInfoGoogle(Map<String, Object> attributes, String emailWord) {
        return new OAuth2UserInfo(
                "google",
                (String) attributes.get("sub"),
                (String) attributes.get(emailWord),
                (String) attributes.get("name")
        );
    }
}
