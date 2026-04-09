package cookbook.stage.backend.auth.shared;

import cookbook.stage.backend.auth.application.OAuth2UserInfo;
import cookbook.stage.backend.shared.domain.OAuth2Exception;
import cookbook.stage.backend.shared.infrastructure.security.CookieUtils;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@NamedInterface
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserApi userApi;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CookieAuthorizationRequestRepository cookieRepo;
    private final CookieUtils cookieUtils;

    @Value("${frontend.url:http://localhost:4200/}")
    private String frontendUrl;
    @Value("${app.jwt.expiration-ms}")
    private long jwtTokenExpiration;
    @Value("${app.jwt.remember-me-expiration-seconds}")
    private long refreshTokenExpiration;

    public OAuth2AuthenticationSuccessHandler(UserApi userApi,
                                              JwtService jwtService,
                                              RefreshTokenService refreshTokenService,
                                              CookieAuthorizationRequestRepository cookieRepo,
                                              CookieUtils cookieUtils) {
        this.userApi = userApi;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.cookieRepo = cookieRepo;
        this.cookieUtils = cookieUtils;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = token.getPrincipal();

        if (oauth2User == null) {
            throw new OAuth2Exception("OAuth2 authentication failed");
        }

        OAuth2UserInfo userInfo = OAuth2UserInfo.from(
                token.getAuthorizedClientRegistrationId(),
                oauth2User.getAttributes()
        );

        User user = userApi.findBySocialConnection(userInfo.provider(), userInfo.providerId())
                .orElseGet(() -> userApi.autoSaveAfterLogin(
                        userInfo.email(), userInfo.name(), userInfo.provider(), userInfo.providerId()
                ));

        boolean rememberMe = cookieUtils.getCookie(request,
                        CookieAuthorizationRequestRepository.REMEMBER_ME_COOKIE_NAME)
                .map(cookie -> Boolean.parseBoolean(cookie.getValue()))
                .orElse(false);

        cookieRepo.removeAuthorizationRequestCookies(request, response);

        String accessToken = jwtService.generateToken(user);
        cookieUtils.addCookie(response, "access_token", accessToken, jwtTokenExpiration / 1000 /* to seconds */, false);

        if (rememberMe) {
            String refreshToken = refreshTokenService.createRefreshToken(user.getId());
            cookieUtils.addCookie(response, "refresh_token", refreshToken, refreshTokenExpiration, true);
        }

        response.sendRedirect(frontendUrl + "auth/callback");
    }
}
