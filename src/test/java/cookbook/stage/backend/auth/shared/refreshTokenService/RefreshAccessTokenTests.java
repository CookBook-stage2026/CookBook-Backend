package cookbook.stage.backend.auth.shared.refreshTokenService;

import cookbook.stage.backend.auth.domain.RefreshToken;
import cookbook.stage.backend.auth.domain.RefreshTokenRepository;
import cookbook.stage.backend.auth.shared.RefreshTokenService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RefreshAccessTokenTests {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserApi userApi;

    @Test
    void refreshAccessToken_TokenNotFound_ThrowsException() {
        // Arrange
        String nonExistentToken = UUID.randomUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> refreshTokenService.refreshAccessToken(nonExistentToken))
                .isInstanceOf(AuthorizationDeniedException.class)
                .hasMessageContaining("Refresh token is not in database!");
    }

    @Test
    void refreshAccessToken_TokenExpired_ThrowsExceptionAndDeleteToken() {
        // Arrange
        User user = userApi.autoSaveAfterLogin("expired@example.com", "Expired User", "google", "exp-123");
        String expiredTokenString = UUID.randomUUID().toString();

        RefreshToken expiredToken = new RefreshToken(
                expiredTokenString,
                user.getId(),
                Instant.now().minusSeconds(3600)
        );
        refreshTokenRepository.save(expiredToken);

        // Act & Assert
        assertThatThrownBy(() -> refreshTokenService.refreshAccessToken(expiredTokenString))
                .isInstanceOf(AuthorizationDeniedException.class)
                .hasMessageContaining("Refresh token was expired");

        // Verify
        assertThat(refreshTokenRepository.findByToken(expiredTokenString)).isEmpty();
    }
}
