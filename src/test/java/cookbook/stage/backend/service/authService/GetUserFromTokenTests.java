package cookbook.stage.backend.service.authService;

import cookbook.stage.backend.domain.auth.OAuth2UserInfo;
import cookbook.stage.backend.domain.exception.OAuth2Exception;
import cookbook.stage.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class GetUserFromTokenTests {
    private static final String PROVIDER_GOOGLE = "google";
    private static final String PROVIDER_UNKNOWN = "yahoo";
    private static final String SUB_ID = "google-123";
    private static final String EMAIL = "google@example.com";
    private static final String NAME = "Google User";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String SUB_KEY = "sub";
    private static final String EMAIL_KEY = "email";
    private static final String NAME_KEY = "name";
    private static final String EXCEPTION_MESSAGE_PREFIX = "Unknown OAuth2 provider: ";

    @Autowired
    private AuthService authService;

    @Test
    void getUserFromToken_ValidProvider_DelegatesToCorrectStrategy() {
        Map<String, Object> attributes = Map.of(
                SUB_KEY, SUB_ID,
                EMAIL_KEY, EMAIL,
                NAME_KEY, NAME
        );
        OAuth2AuthenticationToken token = createAuthenticationToken(PROVIDER_GOOGLE, attributes);

        OAuth2UserInfo userInfo = authService.getUserFromToken(token);

        assertThat(userInfo)
                .returns(PROVIDER_GOOGLE, OAuth2UserInfo::provider)
                .returns(SUB_ID, OAuth2UserInfo::providerId)
                .returns(EMAIL, OAuth2UserInfo::email)
                .returns(NAME, OAuth2UserInfo::name);
    }

    @Test
    void getUserFromToken_UnknownProvider_ThrowsOAuth2Exception() {
        Map<String, Object> attributes = Map.of(SUB_KEY, SUB_ID);
        OAuth2AuthenticationToken token = createAuthenticationToken(PROVIDER_UNKNOWN, attributes);

        assertThatThrownBy(() -> authService.getUserFromToken(token))
                .isInstanceOf(OAuth2Exception.class)
                .hasMessageContaining(EXCEPTION_MESSAGE_PREFIX + PROVIDER_UNKNOWN);
    }

    private OAuth2AuthenticationToken createAuthenticationToken(String provider, Map<String, Object> attributes) {
        OAuth2User oauth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(ROLE_USER)),
                attributes,
                SUB_KEY
        );
        return new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), provider);
    }
}
