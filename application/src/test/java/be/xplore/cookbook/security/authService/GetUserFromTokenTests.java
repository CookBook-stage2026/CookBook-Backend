package be.xplore.cookbook.security.authService;

import be.xplore.cookbook.security.exception.OAuth2Exception;
import be.xplore.cookbook.security.AuthService;
import be.xplore.cookbook.security.strategy.OAuth2UserExtractorRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserFromTokenTests {
    private final AuthService authService = new AuthService(mock(OAuth2UserExtractorRegistry.class));

    @Test
    void getUserFromToken_NullPrincipal_ThrowsOAuth2Exception() {
        OAuth2AuthenticationToken token = mock(OAuth2AuthenticationToken.class);
        when(token.getPrincipal()).thenReturn(null);

        assertThatThrownBy(() -> authService.getUserFromToken(token))
                .isInstanceOf(OAuth2Exception.class);
    }
}
