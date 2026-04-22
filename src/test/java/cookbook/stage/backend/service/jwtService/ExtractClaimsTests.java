package cookbook.stage.backend.service.jwtService;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ExtractClaimsTests {

    @Autowired
    private JwtService jwtService;

    @Test
    void extractClaims_ValidToken_ReturnsPopulatedClaims() {
        // Arrange
        User user = new User(new UserId(UUID.randomUUID()), "claims@example.com", "Claims User", List.of());
        String token = jwtService.generateToken(user);

        // Act
        Claims claims = jwtService.extractClaims(token);

        // Assert
        assertThat(claims).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void extractClaims_MalformedToken_ThrowsJwtException() {
        // Arrange
        String malformedToken = "invalid.token.structure";

        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractClaims(malformedToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void extractClaims_NullToken_ThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractClaims(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
