package cookbook.stage.backend.shared.infrastructure.security.jwtService;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ExtractUserIdTests {

    @Autowired
    private JwtService jwtService;

    @Test
    void extractUserId_ValidToken_ReturnsUserId() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        User user = new User(new UserId(expectedId), "id@example.com", "Id User", List.of());
        String token = jwtService.generateToken(user);

        // Act
        UUID extractedId = jwtService.extractUserId(token);

        // Assert
        assertThat(extractedId).isEqualTo(expectedId);
    }

    @Test
    void extractUserId_InvalidToken_ThrowsJwtException() {
        // Arrange
        String invalidToken = "totally-invalid-token";

        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUserId(invalidToken))
                .isInstanceOf(JwtException.class);
    }
}
