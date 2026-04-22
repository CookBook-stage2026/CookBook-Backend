package cookbook.stage.backend.auth.shared.oAuth2AuthenticationSuccessHandler;

import cookbook.stage.backend.domain.exception.OAuth2Exception;
import cookbook.stage.backend.repository.CookieAuthorizationRequestRepository;
import cookbook.stage.backend.service.UserService;
import cookbook.stage.backend.util.OAuth2AuthenticationSuccessHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class OnAuthenticationSuccessTests {

    @Autowired
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Autowired
    private UserService userService;
    private static final int RANDOM_ID = 9999;

    @Value("${frontend.url:http://localhost:4200/}")
    private String frontendUrl;

    @Test
    void onAuthenticationSuccess_ExistingUser_SetsCookieAndRedirects() throws IOException {
        // Arrange
        userService.autoSaveAfterLogin(
                "existing@example.com", "Existing User", "google", "google-123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie(CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                        "auth-req-data")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, Object> attributes = Map.of(
                "sub", "google-123",
                "email", "existing@example.com",
                "name", "Existing User"
        );
        OAuth2User oauth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")), attributes, "name");
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
                oauth2User, oauth2User.getAuthorities(), "google");

        // Act
        successHandler.onAuthenticationSuccess(request, response, token);

        // Assert
        assertThat(response.getRedirectedUrl()).isEqualTo(frontendUrl + "auth/callback");

        var setCookieHeaders = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeaders)
                .isNotEmpty()
                .anyMatch(header -> header.contains("access_token="))
                .anyMatch(header -> header.contains(
                        CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME + "=")
                        && header.contains("Max-Age=0"));
    }

    @Test
    void onAuthenticationSuccess_NewUser_CreatesUserSetsAccessTokenAndRedirects()
            throws IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, Object> attributes = Map.of(
                "id", RANDOM_ID,
                "email", "newgithub@example.com",
                "login", "githublogin"
        );
        OAuth2User oauth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")), attributes, "login");
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
                oauth2User, oauth2User.getAuthorities(), "github");

        // Act
        successHandler.onAuthenticationSuccess(request, response, token);

        // Assert
        assertThat(userService.findBySocialConnection("github", "9999")).isPresent();

        assertThat(response.getRedirectedUrl()).isEqualTo(frontendUrl + "auth/callback");

        var setCookieHeaders = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeaders)
                .isNotEmpty()
                .anyMatch(header -> header.contains("access_token="));
    }

    @Test
    void onAuthenticationSuccess_NullOAuth2User_ThrowsOAuth2Exception() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthenticationToken tokenMock = mock(OAuth2AuthenticationToken.class);
        when(tokenMock.getPrincipal()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> successHandler.onAuthenticationSuccess(request, response, tokenMock))
                .isInstanceOf(OAuth2Exception.class)
                .hasMessageContaining("OAuth2 authentication failed");
    }
}
