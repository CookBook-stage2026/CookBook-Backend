package be.xplore.cookbook.security.strategy;

import be.xplore.cookbook.core.domain.auth.OAuth2UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GitHubOAuth2UserExtractorStrategy implements OAuth2UserExtractorStrategy {

    @Override
    public OAuth2UserInfo extractUserInfo(Map<String, Object> attributes) {
        String id = String.valueOf(attributes.get("id"));
        String email = (String) attributes.get("email");

        if (email == null || email.isBlank()) {
            email = id + "@github.local";
        }

        String name = attributes.get("name") != null
                ? (String) attributes.get("name")
                : (String) attributes.get("login");

        return new OAuth2UserInfo(getProvider(), id, email, name);
    }

    @Override
    public String getProvider() {
        return "github";
    }
}

