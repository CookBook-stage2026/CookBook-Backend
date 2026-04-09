package cookbook.stage.backend.auth.shared.refreshTokenService;

import cookbook.stage.backend.auth.domain.RefreshToken;
import cookbook.stage.backend.auth.domain.RefreshTokenRepository;
import cookbook.stage.backend.auth.shared.RefreshTokenService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CreateRefreshTokenTests {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserApi userApi;
    private static final int SECONDS_TO_ADD = 100;

    @Test
    void createRefreshToken_ValidUserId_DeletesOldAndCreatesNewToken() {
        // Arrange
        User user = userApi.autoSaveAfterLogin("create@example.com", "Create User", "google", "create-123");

        RefreshToken oldToken = new RefreshToken("old-token-value", user.getId(), Instant.now()
                .plusSeconds(SECONDS_TO_ADD));
        refreshTokenRepository.save(oldToken);

        // Act
        String newTokenString = refreshTokenService.createRefreshToken(user.getId());

        // Assert
        assertThat(newTokenString).isNotBlank().isNotEqualTo("old-token-value");

        Optional<RefreshToken> oldTokenInDb = refreshTokenRepository.findByToken("old-token-value");
        assertThat(oldTokenInDb).isEmpty();

        Optional<RefreshToken> newTokenInDb = refreshTokenRepository.findByToken(newTokenString);
        assertThat(newTokenInDb).isPresent();
        assertThat(newTokenInDb.get().userId()).isEqualTo(user.getId());
        assertThat(newTokenInDb.get().isExpired()).isFalse();
    }
}
