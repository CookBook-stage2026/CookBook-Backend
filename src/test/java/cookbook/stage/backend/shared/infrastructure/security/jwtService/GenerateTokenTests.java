package cookbook.stage.backend.shared.infrastructure.security.jwtService;

import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class GenerateTokenTests {

    @Autowired
    private JwtService jwtService;

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

    @Test
    void generateToken_NullUser_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> jwtService.generateToken(null))
                .isInstanceOf(NullPointerException.class);
    }
}
