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
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IsValidTests {

    private JwtService jwtService;
    private final String secret = Base64.getEncoder()
            .encodeToString("my-256-bit-secret-key-for-jwt-signing!!".getBytes());
    private final long expirationMs = 3600000L;

    private User mockUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", expirationMs);

        mockUser = mock(User.class);
        UserId mockUserId = mock(UserId.class);
        when(mockUserId.id()).thenReturn(UUID.randomUUID());
        when(mockUser.getId()).thenReturn(mockUserId);
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getDisplayName()).thenReturn("Test User");
    }

    @Test
    void shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(mockUser);
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void shouldReturnFalseForMalformedToken() {
        assertThat(jwtService.isValid("malformed")).isFalse();
    }

    @Test
    void shouldReturnFalseForEmptyToken() {
        assertThat(jwtService.isValid("")).isFalse();
    }

    @Test
    void shouldReturnFalseForNullToken() {
        assertThat(jwtService.isValid(null)).isFalse();
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() - 1))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)))
                .compact();

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void shouldReturnFalseForTokenSignedWithDifferentKey() {
        JwtService otherService = new JwtService();
        String otherSecret = Base64.getEncoder()
                .encodeToString("different-secret!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".getBytes());
        ReflectionTestUtils.setField(otherService, "secret", otherSecret);
        ReflectionTestUtils.setField(otherService, "expirationMs", expirationMs);
        String token = otherService.generateToken(mockUser);
        assertThat(jwtService.isValid(token)).isFalse();
    }
}
