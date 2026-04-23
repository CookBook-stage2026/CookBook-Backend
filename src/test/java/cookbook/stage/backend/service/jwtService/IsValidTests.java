package cookbook.stage.backend.service.jwtService;

import cookbook.stage.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class IsValidTests {
    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2";
    private static final long EXPIRATION_MS = 3600000L;

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    @Test
    void isValid_MalformedToken_ReturnsFalse() {
        assertThat(jwtService.isValid("invalid.token.structure")).isFalse();
    }

    @Test
    void isValid_NullToken_ReturnsFalse() {
        assertThat(jwtService.isValid(null)).isFalse();
    }
}
