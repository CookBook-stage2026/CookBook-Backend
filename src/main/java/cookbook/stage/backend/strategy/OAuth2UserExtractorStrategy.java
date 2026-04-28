package cookbook.stage.backend.strategy;

import be.xplore.cookbook.core.domain.auth.OAuth2UserInfo;

import java.util.Map;

public interface OAuth2UserExtractorStrategy {
    /**
     * Extract OAuth2UserInfo from provider attributes.
     *
     * @param attributes The attributes map from the OAuth2 principal
     * @return OAuth2UserInfo containing provider, id, email, and name
     */
    OAuth2UserInfo extractUserInfo(Map<String, Object> attributes);

    /**
     * Get the provider name that this strategy handles.
     *
     * @return The provider identifier (e.g., "google", "github", "microsoft")
     */
    String getProvider();
}

