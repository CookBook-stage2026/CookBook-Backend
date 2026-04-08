package cookbook.stage.backend.shared.infrastructure.security.jwtService;

import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExtractUserIdTests {

    private JwtService jwtService;
    private final String secret = Base64.getEncoder()
            .encodeToString("my-256-bit-secret-key-for-jwt-signing!!".getBytes());
    private final long expirationMs = 3600000L;

    private User mockUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", expirationMs);

        userId = UUID.randomUUID();
        mockUser = mock(User.class);
        UserId mockUserId = mock(UserId.class);
        when(mockUserId.id()).thenReturn(userId);
        when(mockUser.getId()).thenReturn(mockUserId);
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getDisplayName()).thenReturn("Test User");
    }

    @Test
    void shouldExtractCorrectUserIdFromValidToken() {
        String token = jwtService.generateToken(mockUser);
        UUID extracted = jwtService.extractUserId(token);
        assertThat(extracted).isEqualTo(userId);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSubjectIsNotUUID() {
        String tokenWithInvalidSubject = Jwts.builder()
                .subject("not-a-uuid")
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)))
                .compact();

        assertThatThrownBy(() -> jwtService.extractUserId(tokenWithInvalidSubject))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldPropagateJwtExceptionForInvalidToken() {
        assertThatThrownBy(() -> jwtService.extractUserId("invalid.token"))
                .isInstanceOf(io.jsonwebtoken.JwtException.class);
    }
}
