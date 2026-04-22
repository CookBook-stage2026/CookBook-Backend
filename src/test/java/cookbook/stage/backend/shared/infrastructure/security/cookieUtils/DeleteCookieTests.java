package cookbook.stage.backend.shared.infrastructure.security.cookieUtils;

import cookbook.stage.backend.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DeleteCookieTests {

    @Autowired
    private CookieUtils cookieUtils;

    @Test
    void deleteCookie_CookieExists_SetsMaxAgeToZero() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String cookieName = "session_cookie";
        request.setCookies(new Cookie(cookieName, "active_session"));

        // Act
        cookieUtils.deleteCookie(request, response, cookieName);

        // Assert
        String header = response.getHeader(HttpHeaders.SET_COOKIE);
        assertThat(header)
                .isNotNull()
                .contains(cookieName + "=")
                .contains("Max-Age=0");
    }

    @Test
    void deleteCookie_CookieDoesNotExist_DoesNotSetHeader() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie("other_cookie", "value"));

        // Act
        cookieUtils.deleteCookie(request, response, "non_existent_cookie");

        // Assert
        String header = response.getHeader(HttpHeaders.SET_COOKIE);
        assertThat(header).isNull();
    }
}
