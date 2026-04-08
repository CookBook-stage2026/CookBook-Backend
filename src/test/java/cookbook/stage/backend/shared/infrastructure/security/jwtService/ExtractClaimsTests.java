package cookbook.stage.backend.shared.infrastructure.security.jwtService;


import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExtractClaimsTests {

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
    void shouldExtractClaimsFromValidToken() {
        String token = jwtService.generateToken(mockUser);
        Claims claims = jwtService.extractClaims(token);
        assertThat(claims).isNotNull();
    }

    @Test
    void shouldThrowJwtExceptionForMalformedToken() {
        assertThatThrownBy(() -> jwtService.extractClaims("not.a.jwt"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldThrowJwtExceptionForEmptyToken() {
        assertThatThrownBy(() -> jwtService.extractClaims(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowJwtExceptionForExpiredToken() {
        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() - 1))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)))
                .compact();

        assertThatThrownBy(() -> jwtService.extractClaims(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void shouldThrowJwtExceptionForTokenSignedWithDifferentKey() {
        JwtService otherService = new JwtService();
        String otherSecret = Base64.getEncoder()
                .encodeToString("different-secret!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".getBytes());
        ReflectionTestUtils.setField(otherService, "secret", otherSecret);
        ReflectionTestUtils.setField(otherService, "expirationMs", expirationMs);
        String token = otherService.generateToken(mockUser);

        assertThatThrownBy(() -> jwtService.extractClaims(token))
                .isInstanceOf(JwtException.class);
    }
}
