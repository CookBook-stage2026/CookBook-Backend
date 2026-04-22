package cookbook.stage.backend.shared.infrastructure.security.cookieUtils;

import cookbook.stage.backend.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GetCookieTests {

    @Autowired
    private CookieUtils cookieUtils;

    @Test
    void getCookie_CookieExists_ReturnsCookie() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("target_cookie", "target_value"));

        // Act
        Optional<Cookie> result = cookieUtils.getCookie(request, "target_cookie");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo("target_value");
    }

    @Test
    void getCookie_CookieMissing_ReturnsEmpty() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other_cookie", "other_value"));

        // Act
        Optional<Cookie> result = cookieUtils.getCookie(request, "target_cookie");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getCookie_NoCookiesInRequest_ReturnsEmpty() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act
        Optional<Cookie> result = cookieUtils.getCookie(request, "target_cookie");

        // Assert
        assertThat(result).isEmpty();
    }
}
