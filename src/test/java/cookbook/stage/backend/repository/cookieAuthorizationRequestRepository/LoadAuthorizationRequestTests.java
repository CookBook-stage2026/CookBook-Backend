package cookbook.stage.backend.repository.cookieAuthorizationRequestRepository;

import cookbook.stage.backend.repository.CookieAuthorizationRequestRepository;
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
class LoadAuthorizationRequestTests {

    @Autowired
    private CookieAuthorizationRequestRepository repository;

    @Test
    void loadAuthorizationRequest_ValidCookieAndSignature_ReturnsRequest() {
        // Arrange
        MockHttpServletRequest saveRequest = new MockHttpServletRequest();
        MockHttpServletResponse saveResponse = new MockHttpServletResponse();
        OAuth2AuthorizationRequest originalAuthRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId("test-client")
                .authorizationUri("http://example.com/auth")
                .state("test-state")
                .build();

        repository.saveAuthorizationRequest(originalAuthRequest, saveRequest, saveResponse);

        String cookieHeader = saveResponse.getHeader(HttpHeaders.SET_COOKIE);
        assert cookieHeader != null;
        String signedCookieValue = extractCookieValue(cookieHeader,
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);

        MockHttpServletRequest loadRequest = new MockHttpServletRequest();
        loadRequest.setCookies(new Cookie(CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                signedCookieValue));

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
        MockHttpServletRequest saveRequest = new MockHttpServletRequest();
        MockHttpServletResponse saveResponse = new MockHttpServletResponse();
        OAuth2AuthorizationRequest originalAuthRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId("test-client")
                .authorizationUri("http://example.com/auth")
                .state("test-state")
                .build();

        repository.saveAuthorizationRequest(originalAuthRequest, saveRequest, saveResponse);
        String cookieHeader = saveResponse.getHeader(HttpHeaders.SET_COOKIE);
        assert cookieHeader != null;
        String originalCookieValue = extractCookieValue(cookieHeader,
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);

        String[] parts = originalCookieValue.split("\\.");
        String tamperedCookieValue = parts[0] + ".this-is-an-invalid-signature";

        MockHttpServletRequest loadRequest = new MockHttpServletRequest();
        loadRequest.setCookies(new Cookie(CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                tamperedCookieValue));

        // Act
        OAuth2AuthorizationRequest loadedRequest = repository.loadAuthorizationRequest(loadRequest);

        // Assert
        assertThat(loadedRequest).isNull();
    }

    private String extractCookieValue(String header, String cookieName) {
        String prefix = cookieName + "=";
        int start = header.indexOf(prefix) + prefix.length();
        int end = header.indexOf(";", start);
        return end == -1 ? header.substring(start) : header.substring(start, end);
    }
}
