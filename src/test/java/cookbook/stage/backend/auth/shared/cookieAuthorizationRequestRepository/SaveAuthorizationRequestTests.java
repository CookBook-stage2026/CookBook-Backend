package cookbook.stage.backend.auth.shared.cookieAuthorizationRequestRepository;

import cookbook.stage.backend.auth.shared.CookieAuthorizationRequestRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SaveAuthorizationRequestTests {

    @Autowired
    private CookieAuthorizationRequestRepository repository;

    @Test
    void saveAuthorizationRequest_ValidRequest_SetsCookieWithSignature() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId("test-client")
                .authorizationUri("http://example.com/auth")
                .state("test-state")
                .build();

        // Act
        repository.saveAuthorizationRequest(authRequest, request, response);

        // Assert
        String setCookieHeader = response.getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeader)
                .isNotNull()
                .contains(CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);

        String cookieValue = extractCookieValue(setCookieHeader,
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        assertThat(cookieValue).contains(".");
    }

    @Test
    void saveAuthorizationRequest_NullRequest_DeletesCookies() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setCookies(
                new Cookie(CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                        "existing-value"),
                new Cookie(CookieAuthorizationRequestRepository.REMEMBER_ME_COOKIE_NAME, "true")
        );

        // Act
        repository.saveAuthorizationRequest(null, request, response);

        // Assert
        var setCookieHeaders = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeaders)
                .isNotEmpty()
                .anyMatch(header -> header.contains(
                        CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME + "=")
                        && header.contains("Max-Age=0"))
                .anyMatch(header -> header.contains(CookieAuthorizationRequestRepository.REMEMBER_ME_COOKIE_NAME + "=")
                        && header.contains("Max-Age=0"));    }

    private String extractCookieValue(String header, String cookieName) {
        String prefix = cookieName + "=";
        int start = header.indexOf(prefix) + prefix.length();
        int end = header.indexOf(";", start);
        return end == -1 ? header.substring(start) : header.substring(start, end);
    }
}
