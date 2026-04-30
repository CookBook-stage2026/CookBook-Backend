package be.xplore.cookbook.security.strategy;

import be.xplore.cookbook.core.domain.auth.OAuth2UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleOAuth2UserExtractorStrategy implements OAuth2UserExtractorStrategy {

    @Override
    public OAuth2UserInfo extractUserInfo(Map<String, Object> attributes) {
        return new OAuth2UserInfo(
                getProvider(),
                (String) attributes.get("sub"),
                (String) attributes.get("email"),
                (String) attributes.get("name")
        );
    }

    @Override
    public String getProvider() {
        return "google";
    }
}

