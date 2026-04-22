package cookbook.stage.backend.auth.application;

import cookbook.stage.backend.domain.auth.OAuth2UserInfo;
import cookbook.stage.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetUserFromTokenTests {
    private static final int RANDOM_ID = 98765;
    private final AuthService authService = new AuthService();

    @Test
    void getUserFromToken_GoogleProvider_ReturnsCorrectUserInfo() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "sub", "google-123",
                "email", "google@example.com",
                "name", "Google User"
        );
        OAuth2AuthenticationToken token = createAuthenticationToken("google", attributes);

        // Act
        OAuth2UserInfo userInfo = authService.getUserFromToken(token);

        // Assert
        assertThat(userInfo.provider()).isEqualTo("google");
        assertThat(userInfo.providerId()).isEqualTo("google-123");
        assertThat(userInfo.email()).isEqualTo("google@example.com");
        assertThat(userInfo.name()).isEqualTo("Google User");
    }

    @Test
    void getUserFromToken_GithubProviderWithEmail_ReturnsCorrectUserInfo() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "id", RANDOM_ID,
                "email", "github@example.com",
                "login", "githublogin"
        );
        OAuth2AuthenticationToken token = createAuthenticationToken("github", attributes);

        // Act
        OAuth2UserInfo userInfo = authService.getUserFromToken(token);

        // Assert
        assertThat(userInfo.provider()).isEqualTo("github");
        assertThat(userInfo.providerId()).isEqualTo("98765");
        assertThat(userInfo.email()).isEqualTo("github@example.com");
        assertThat(userInfo.name()).isEqualTo("githublogin");
    }

    @Test
    void getUserFromToken_GithubProviderWithoutEmail_AssignsFallbackEmail() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "id", RANDOM_ID,
                "name", "Github User"
        );
        OAuth2AuthenticationToken token = createAuthenticationToken("github", attributes);

        // Act
        OAuth2UserInfo userInfo = authService.getUserFromToken(token);

        // Assert
        assertThat(userInfo.email()).isEqualTo("98765@github.local");
        assertThat(userInfo.name()).isEqualTo("Github User");
    }

    @Test
    void getUserFromToken_MicrosoftProvider_ReturnsCorrectUserInfo() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "sub", "ms-123",
                "email", "ms@example.com",
                "name", "MS User"
        );
        OAuth2AuthenticationToken token = createAuthenticationToken("microsoft", attributes);

        // Act
        OAuth2UserInfo userInfo = authService.getUserFromToken(token);

        // Assert
        assertThat(userInfo.provider()).isEqualTo("microsoft");
        assertThat(userInfo.providerId()).isEqualTo("ms-123");
        assertThat(userInfo.email()).isEqualTo("ms@example.com");
        assertThat(userInfo.name()).isEqualTo("MS User");
    }

    @Test
    void getUserFromToken_UnknownProvider_ThrowsIllegalArgumentException() {
        // Arrange
        Map<String, Object> attributes = Map.of("sub", "123");
        OAuth2AuthenticationToken token = createAuthenticationToken("yahoo", attributes);

        // Act & Assert
        assertThatThrownBy(() -> authService.getUserFromToken(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown provider: yahoo");
    }

    private OAuth2AuthenticationToken createAuthenticationToken(String provider, Map<String, Object> attributes) {
        OAuth2User oauth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                switch (provider) {
                    case "google", "microsoft" -> "sub";
                    case "github" -> "id";
                    default -> "sub";
                }
        );
        return new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), provider);
    }
}
