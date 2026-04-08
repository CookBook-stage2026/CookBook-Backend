package cookbook.stage.backend.auth.application.refreshTokenService;

import cookbook.stage.backend.auth.api.dto.TokenRefreshResponse;
import cookbook.stage.backend.auth.application.RefreshTokenService;
import cookbook.stage.backend.auth.domain.RefreshToken;
import cookbook.stage.backend.auth.domain.RefreshTokenRepository;
import cookbook.stage.backend.auth.infrastructure.RefreshTokenRepositoryImpl;
import cookbook.stage.backend.auth.infrastructure.jpa.JpaRefreshTokenRepository;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import cookbook.stage.backend.user.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RefreshTokenService.class, RefreshTokenRepositoryImpl.class})
class RefreshAccessTokenTests {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JpaRefreshTokenRepository jpaRefreshTokenRepository;

    @MockitoBean
    private UserApi userApi;

    @MockitoBean
    private JwtService jwtService;

    private final int subtractionMinutes = 3600;

    @BeforeEach
    void tearDown() {
        jpaRefreshTokenRepository.deleteAll();
    }

    @Test
    void refreshAccessToken_shouldReturnNewAccessToken_whenTokenIsValid() {
        UserId userId = new UserId(UUID.randomUUID());
        String requestToken = refreshTokenService.createRefreshToken(userId);
        User user = mock(User.class);
        when(userApi.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("new-access-token");

        TokenRefreshResponse response = refreshTokenService.refreshAccessToken(requestToken);

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo(requestToken);
    }

    @Test
    void refreshAccessToken_shouldThrow_whenTokenNotFound() {
        String invalidToken = UUID.randomUUID().toString();
        when(refreshTokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.refreshAccessToken(invalidToken))
                .isInstanceOf(AuthorizationDeniedException.class);

        verify(refreshTokenRepository).findByToken(invalidToken);
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void refreshAccessToken_shouldThrowAndDeleteToken_whenTokenIsExpired() {
        UserId userId = new UserId(UUID.randomUUID());
        String token = UUID.randomUUID().toString();
        refreshTokenRepository.save(new RefreshToken(token, userId, Instant.now().minusSeconds(subtractionMinutes)));

        assertThatThrownBy(() -> refreshTokenService.refreshAccessToken(token))
                .isInstanceOf(AuthorizationDeniedException.class);

        assertThat(jpaRefreshTokenRepository.findByToken(token)).isEmpty();
    }

    @Test
    void refreshAccessToken_shouldThrow_whenUserNotFound() {
        UserId userId = new UserId(UUID.randomUUID());
        String requestToken = refreshTokenService.createRefreshToken(userId);
        when(userApi.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.refreshAccessToken(requestToken))
                .isInstanceOf(AuthorizationDeniedException.class);
    }
}
