package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.auth.OAuth2UserInfo;
import be.xplore.cookbook.core.domain.exception.OAuth2Exception;
import cookbook.stage.backend.strategy.OAuth2UserExtractorRegistry;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final OAuth2UserExtractorRegistry extractorRegistry;

    public AuthService(OAuth2UserExtractorRegistry extractorRegistry) {
        this.extractorRegistry = extractorRegistry;
    }

    /**
     * Extract OAuth2 user information from an authentication token.
     * Delegates to the appropriate provider strategy based on the registered provider.
     *
     * @param token The OAuth2 authentication token
     * @return OAuth2UserInfo containing provider, id, email, and name
     * @throws OAuth2Exception if the principal is null or provider is unknown
     */
    public OAuth2UserInfo getUserFromToken(OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        var principal = token.getPrincipal();

        if (principal == null) {
            throw new OAuth2Exception("User could not be found in the token.");
        }

        var strategy = extractorRegistry.getStrategy(provider);
        return strategy.extractUserInfo(principal.getAttributes());
    }
}
