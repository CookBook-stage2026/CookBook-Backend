package cookbook.stage.backend.shared.infrastructure.security.cookieUtils;

import cookbook.stage.backend.shared.infrastructure.security.CookieUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AddCookieTests {

    @Autowired
    private CookieUtils cookieUtils;

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void addCookie_ValidData_SetsCookieHeaderCorrectly() {
        // Arrange
        MockHttpServletResponse response = new MockHttpServletResponse();
        String name = "access_token";
        String value = "jwt-value";
        long maxAge = 3600L;

        // Act
        cookieUtils.addCookie(response, name, value, maxAge, true);

        // Assert
        String header = response.getHeader(HttpHeaders.SET_COOKIE);
        assertThat(header)
                .isNotNull()
                .contains(name + "=" + value)
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Lax")
                .contains("Max-Age=" + maxAge);
    }

    @Test
    void addCookie_NullName_ThrowsIllegalArgumentException() {
        // Arrange
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act & Assert
        assertThatThrownBy(() -> cookieUtils.addCookie(response, null, "value", 3600L, true))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
