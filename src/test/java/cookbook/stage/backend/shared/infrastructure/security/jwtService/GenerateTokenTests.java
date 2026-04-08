package cookbook.stage.backend.shared.infrastructure.security.jwtService;

import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenerateTokenTests {

    private JwtService jwtService;
    private final String secret = Base64.getEncoder()
            .encodeToString("my-256-bit-secret-key-for-jwt-signing!!".getBytes());
    private final long expirationMs = 3600000L; // 1 hour
    private final long leniency = 10000;

    private User mockUser;
    private UUID userId;
    private String userEmail;
    private String userDisplayName;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", expirationMs);

        userId = UUID.randomUUID();
        userEmail = "test@example.com";
        userDisplayName = "Test User";

        mockUser = mock(User.class);
        UserId mockUserId = mock(UserId.class);
        when(mockUserId.id()).thenReturn(userId);
        when(mockUser.getId()).thenReturn(mockUserId);
        when(mockUser.getEmail()).thenReturn(userEmail);
        when(mockUser.getDisplayName()).thenReturn(userDisplayName);
    }

    @Test
    void shouldCreateValidJwtWithCorrectClaims() {
        String token = jwtService.generateToken(mockUser);

        assertThat(token).isNotBlank();
        Claims claims = jwtService.extractClaims(token);

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("email", String.class)).isEqualTo(userEmail);
        assertThat(claims.get("name", String.class)).isEqualTo(userDisplayName);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();

        long expectedExpiration = claims.getIssuedAt().getTime() + expirationMs;
        assertThat(claims.getExpiration().getTime()).isEqualTo(expectedExpiration);
    }

    @Test
    void shouldSetIssuedAtToCurrentTime() {
        long before = System.currentTimeMillis();
        String token = jwtService.generateToken(mockUser);
        long after = System.currentTimeMillis();

        Claims claims = jwtService.extractClaims(token);
        long issuedAtMs = claims.getIssuedAt().getTime();

        assertThat(issuedAtMs).isBetween(before - leniency, after + leniency);
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {
        User otherUser = mock(User.class);
        UserId otherUserId = mock(UserId.class);
        when(otherUserId.id()).thenReturn(UUID.randomUUID());
        when(otherUser.getId()).thenReturn(otherUserId);
        when(otherUser.getEmail()).thenReturn("other@example.com");
        when(otherUser.getDisplayName()).thenReturn("Other User");

        String token1 = jwtService.generateToken(mockUser);
        String token2 = jwtService.generateToken(otherUser);

        assertThat(token1).isNotEqualTo(token2);
    }
}
