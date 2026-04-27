package cookbook.stage.backend.util.oAuth2AuthenticationSuccessHandler;

import cookbook.stage.backend.domain.exception.OAuth2Exception;
import cookbook.stage.backend.repository.CookieAuthorizationRequestRepository;
import cookbook.stage.backend.service.AuthService;
import cookbook.stage.backend.service.JwtService;
import cookbook.stage.backend.service.UserService;
import cookbook.stage.backend.util.CookieUtils;
import cookbook.stage.backend.util.OAuth2AuthenticationSuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OnAuthenticationSuccessTests {

    private final OAuth2AuthenticationSuccessHandler successHandler = new OAuth2AuthenticationSuccessHandler(
            mock(UserService.class),
            mock(JwtService.class),
            mock(CookieAuthorizationRequestRepository.class),
            mock(CookieUtils.class),
            mock(AuthService.class)
    );

    @Test
    void onAuthenticationSuccess_NullOAuth2User_ThrowsOAuth2Exception() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthenticationToken tokenMock = mock(OAuth2AuthenticationToken.class);
        when(tokenMock.getPrincipal()).thenReturn(null);

        assertThatThrownBy(() ->
                successHandler.onAuthenticationSuccess(request, response, tokenMock)
        )
                .isInstanceOf(OAuth2Exception.class)
                .hasMessageContaining("OAuth2 authentication failed");
    }
}
