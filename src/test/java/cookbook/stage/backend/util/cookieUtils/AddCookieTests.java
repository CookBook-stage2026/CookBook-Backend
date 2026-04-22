package cookbook.stage.backend.util.cookieUtils;

import cookbook.stage.backend.util.CookieUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AddCookieTests {
    private static final long MAX_AGE = 3600L;
    @Autowired
    private CookieUtils cookieUtils;

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void addCookie_ValidData_SetsCookieHeaderCorrectly() {
        // Arrange
        MockHttpServletResponse response = new MockHttpServletResponse();
        String name = "access_token";
        String value = "jwt-value";

        // Act
        cookieUtils.addCookie(response, name, value, MAX_AGE, true);

        // Assert
        String header = response.getHeader(HttpHeaders.SET_COOKIE);
        assertThat(header)
                .isNotNull()
                .contains(name + "=" + value)
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Lax")
                .contains("Max-Age=" + MAX_AGE);
    }

    @Test
    void addCookie_NullName_ThrowsIllegalArgumentException() {
        // Arrange
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act & Assert
        assertThatThrownBy(() -> cookieUtils.addCookie(response, null, "value", MAX_AGE, true))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
