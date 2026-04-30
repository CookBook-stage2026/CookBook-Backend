package be.xplore.cookbook.security.oAuth2SuccessHandler;

import be.xplore.cookbook.security.exception.OAuth2Exception;
import be.xplore.cookbook.security.AuthService;
import be.xplore.cookbook.security.CookieUtils;
import be.xplore.cookbook.security.JwtService;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.security.CookieAuthorizationRequestRepository;
import be.xplore.cookbook.security.OAuth2AuthenticationSuccessHandler;
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
