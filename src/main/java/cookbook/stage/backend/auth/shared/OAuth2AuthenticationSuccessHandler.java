package cookbook.stage.backend.auth.shared;

import cookbook.stage.backend.auth.api.dto.AuthResponse;
import cookbook.stage.backend.auth.application.OAuth2UserInfo;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserApi userApi;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JsonMapper objectMapper = new JsonMapper();

    public OAuth2AuthenticationSuccessHandler(UserApi userApi,
                                              JwtService jwtService,
                                              RefreshTokenService refreshTokenService) {
        this.userApi = userApi;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = token.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();
        String provider = token.getAuthorizedClientRegistrationId();

        OAuth2UserInfo userInfo = OAuth2UserInfo.from(provider, attributes);

        // Find or create user
        User user = userApi.findBySocialConnection(userInfo.provider(), userInfo.providerId())
                .orElseGet(() -> userApi.autoSaveAfterLogin(
                        userInfo.email(),
                        userInfo.name(),
                        userInfo.provider(),
                        userInfo.providerId()
                ));

        String accessToken = jwtService.generateToken(user);

        boolean rememberMe = Boolean.parseBoolean(request.getParameter("rememberMe"));
        String refreshToken = null;
        if (rememberMe) {
            refreshToken = refreshTokenService.createRefreshToken(user.getId());
        }

        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getDisplayName());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), authResponse);
    }
}
