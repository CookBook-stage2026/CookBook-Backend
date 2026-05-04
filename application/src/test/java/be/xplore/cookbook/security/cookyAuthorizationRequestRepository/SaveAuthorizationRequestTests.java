package be.xplore.cookbook.security.cookyAuthorizationRequestRepository;

import be.xplore.cookbook.security.CookieAuthorizationRequestRepository;
import be.xplore.cookbook.security.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SaveAuthorizationRequestTests {

    private final CookieAuthorizationRequestRepository repository = new CookieAuthorizationRequestRepository(
            new CookieUtils(),
            "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2"
    );

    @Test
    void saveAuthorizationRequest_NullRequest_DeletesCookies() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setCookies(new Cookie(
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "existing-value"));

        // Act
        repository.saveAuthorizationRequest(null, request, response);

        // Assert
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE))
                .isNotEmpty()
                .anyMatch(header ->
                        header.contains(CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                                && header.contains("Max-Age=0"));
    }
}
