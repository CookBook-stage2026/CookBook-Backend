package cookbook.stage.backend.auth.application.refreshTokenService;

import cookbook.stage.backend.auth.application.RefreshTokenService;
import cookbook.stage.backend.auth.infrastructure.RefreshTokenRepositoryImpl;
import cookbook.stage.backend.auth.infrastructure.jpa.JpaRefreshTokenRepository;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.UserApi;
import cookbook.stage.backend.user.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RefreshTokenService.class, RefreshTokenRepositoryImpl.class})
class CreateRefreshTokenTests {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JpaRefreshTokenRepository jpaRefreshTokenRepository;

    @MockitoBean
    private UserApi userApi;

    @MockitoBean
    private JwtService jwtService;

    @BeforeEach
    void tearDown() {
        jpaRefreshTokenRepository.deleteAll();
    }

    @Test
    void createRefreshToken_shouldReturnNonBlankToken() {
        UserId userId = new UserId(UUID.randomUUID());

        String token = refreshTokenService.createRefreshToken(userId);

        assertThat(token).isNotBlank();
    }

    @Test
    void createRefreshToken_shouldPersistTokenToDatabase() {
        UserId userId = new UserId(UUID.randomUUID());

        String token = refreshTokenService.createRefreshToken(userId);

        assertThat(jpaRefreshTokenRepository.findByToken(token)).isPresent();
    }

    @Test
    void createRefreshToken_shouldDeleteExistingToken_beforeSavingNewOne() {
        UserId userId = new UserId(UUID.randomUUID());
        refreshTokenService.createRefreshToken(userId);

        refreshTokenService.createRefreshToken(userId);

        assertThat(jpaRefreshTokenRepository.findAll())
                .filteredOn(t -> t.toDomain().userId().equals(userId))
                .hasSize(1);
    }
}
