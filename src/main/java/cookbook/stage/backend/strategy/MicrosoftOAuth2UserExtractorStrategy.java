package cookbook.stage.backend.strategy;

import cookbook.stage.backend.domain.auth.OAuth2UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MicrosoftOAuth2UserExtractorStrategy implements OAuth2UserExtractorStrategy {

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
        return "microsoft";
    }
}

