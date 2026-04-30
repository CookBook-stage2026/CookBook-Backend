package be.xplore.cookbook.security.cookyAuthorizationRequestRepository;

import be.xplore.cookbook.security.CookieAuthorizationRequestRepository;
import be.xplore.cookbook.security.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static org.assertj.core.api.Assertions.assertThat;

class LoadAuthorizationRequestTests {

    private final CookieAuthorizationRequestRepository repository = new CookieAuthorizationRequestRepository(
            new CookieUtils(),
            "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2"
    );

    private static final OAuth2AuthorizationRequest AUTH_REQUEST = OAuth2AuthorizationRequest.authorizationCode()
            .clientId("test-client")
            .authorizationUri("http://example.com/auth")
            .state("test-state")
            .build();

    @Test
    void loadAuthorizationRequest_ValidCookieAndSignature_ReturnsRequest() {
        // Arrange
        MockHttpServletResponse saveResponse = new MockHttpServletResponse();
        repository.saveAuthorizationRequest(AUTH_REQUEST, new MockHttpServletRequest(), saveResponse);

        MockHttpServletRequest loadRequest = new MockHttpServletRequest();
        loadRequest.setCookies(new Cookie(
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                extractCookieValue(saveResponse)));

        // Act
        OAuth2AuthorizationRequest loadedRequest = repository.loadAuthorizationRequest(loadRequest);

        // Assert
        assertThat(loadedRequest)
                .isNotNull()
                .returns("test-state", OAuth2AuthorizationRequest::getState)
                .returns("test-client", OAuth2AuthorizationRequest::getClientId);
    }

    @Test
    void loadAuthorizationRequest_TamperedSignature_ReturnsNull() {
        // Arrange
        MockHttpServletResponse saveResponse = new MockHttpServletResponse();
        repository.saveAuthorizationRequest(AUTH_REQUEST, new MockHttpServletRequest(), saveResponse);

        String[] parts = extractCookieValue(saveResponse).split("\\.");
        String tampered = parts[0] + ".invalidsignature";

        MockHttpServletRequest loadRequest = new MockHttpServletRequest();
        loadRequest.setCookies(new Cookie(
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, tampered));

        // Act
        OAuth2AuthorizationRequest loadedRequest = repository.loadAuthorizationRequest(loadRequest);

        // Assert
        assertThat(loadedRequest).isNull();
    }

    private String extractCookieValue(MockHttpServletResponse response) {
        String header = response.getHeader(HttpHeaders.SET_COOKIE);
        String prefix = CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME + "=";
        assert header != null;
        int start = header.indexOf(prefix) + prefix.length();
        int end = header.indexOf(";", start);
        return end == -1 ? header.substring(start) : header.substring(start, end);
    }
}
