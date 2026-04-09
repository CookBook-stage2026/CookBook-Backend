package cookbook.stage.backend.auth.domain.OAuth2UserInfo;

import cookbook.stage.backend.auth.application.OAuth2UserInfo;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth2UserInfoFromTests {
    private static final int RANDOM_ID = 98765;

    @Test
    void from_GoogleProvider_ReturnsCorrectUserInfo() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "sub", "google-123",
                "email", "google@example.com",
                "name", "Google User"
        );

        // Act
        OAuth2UserInfo userInfo = OAuth2UserInfo.from("google", attributes);

        // Assert
        assertThat(userInfo.provider()).isEqualTo("google");
        assertThat(userInfo.providerId()).isEqualTo("google-123");
        assertThat(userInfo.email()).isEqualTo("google@example.com");
        assertThat(userInfo.name()).isEqualTo("Google User");
    }

    @Test
    void from_GithubProviderWithEmail_ReturnsCorrectUserInfo() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "id", RANDOM_ID,
                "email", "github@example.com",
                "login", "githublogin"
        );

        // Act
        OAuth2UserInfo userInfo = OAuth2UserInfo.from("github", attributes);

        // Assert
        assertThat(userInfo.provider()).isEqualTo("github");
        assertThat(userInfo.providerId()).isEqualTo("98765");
        assertThat(userInfo.email()).isEqualTo("github@example.com");
        assertThat(userInfo.name()).isEqualTo("githublogin");
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void from_GithubProviderWithoutEmail_AssignsFallbackEmail() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "id", RANDOM_ID,
                "name", "Github User"
        );

        // Act
        OAuth2UserInfo userInfo = OAuth2UserInfo.from("github", attributes);

        // Assert
        assertThat(userInfo.email()).isEqualTo("98765@github.local");
        assertThat(userInfo.name()).isEqualTo("Github User");
    }

    @Test
    void from_MicrosoftProvider_ReturnsCorrectUserInfo() {
        // Arrange
        Map<String, Object> attributes = Map.of(
                "sub", "ms-123",
                "email", "ms@example.com",
                "name", "MS User"
        );

        // Act
        OAuth2UserInfo userInfo = OAuth2UserInfo.from("microsoft", attributes);

        // Assert
        assertThat(userInfo.provider()).isEqualTo("microsoft");
        assertThat(userInfo.providerId()).isEqualTo("ms-123");
    }

    @Test
    void from_UnknownProvider_ThrowsIllegalArgumentException() {
        // Arrange
        Map<String, Object> attributes = Map.of("sub", "123");

        // Act & Assert
        assertThatThrownBy(() -> OAuth2UserInfo.from("yahoo", attributes))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown provider: yahoo");
    }
}
