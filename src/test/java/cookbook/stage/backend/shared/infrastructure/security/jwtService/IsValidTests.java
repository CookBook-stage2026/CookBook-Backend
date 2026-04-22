package cookbook.stage.backend.shared.infrastructure.security.jwtService;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IsValidTests {

    @Autowired
    private JwtService jwtService;

    @Test
    void isValid_ValidToken_ReturnsTrue() {
        // Arrange
        User user = new User(new UserId(UUID.randomUUID()), "valid@example.com", "Valid User", List.of());
        String token = jwtService.generateToken(user);

        // Act
        boolean result = jwtService.isValid(token);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void isValid_MalformedToken_ReturnsFalse() {
        // Arrange
        String malformedToken = "invalid.token.structure";

        // Act
        boolean result = jwtService.isValid(malformedToken);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isValid_NullToken_ReturnsFalse() {
        // Act
        boolean result = jwtService.isValid(null);

        // Assert
        assertThat(result).isFalse();
    }
}
