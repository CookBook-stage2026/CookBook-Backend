package cookbook.stage.backend.service.jwtService;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateTokenTests {
    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2";
    private static final long EXPIRATION_MS = 3600000L;

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    @Test
    void generateToken_ValidUser_TokenGeneratedWithCorrectClaims() {
        // Arrange
        UUID rawId = UUID.randomUUID();
        User user = new User(new UserId(rawId), "test@example.com", "Test User", List.of());

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertThat(token).isNotBlank();
        Claims claims = jwtService.extractClaims(token);
        assertThat(claims)
                .containsEntry("sub", rawId.toString())
                .containsEntry("email", "test@example.com")
                .containsEntry("name", "Test User");
    }
}
