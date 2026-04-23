package cookbook.stage.backend.util;

import cookbook.stage.backend.domain.auth.OAuth2UserInfo;
import cookbook.stage.backend.domain.exception.OAuth2Exception;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.repository.CookieAuthorizationRequestRepository;
import cookbook.stage.backend.service.AuthService;
import cookbook.stage.backend.service.JwtService;
import cookbook.stage.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final CookieAuthorizationRequestRepository cookieRepo;
    private final CookieUtils cookieUtils;
    private final AuthService authService;

    @Value("${frontend.url:http://localhost:4200/}")
    private String frontendUrl;
    @Value("${app.jwt.expiration-ms}")
    private long jwtTokenExpiration;
    private static final long TO_SECONDS = 1000;

    public OAuth2AuthenticationSuccessHandler(UserService userService,
                                              JwtService jwtService,
                                              CookieAuthorizationRequestRepository cookieRepo,
                                              CookieUtils cookieUtils, AuthService authService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.cookieRepo = cookieRepo;
        this.cookieUtils = cookieUtils;
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = token.getPrincipal();

        if (oauth2User == null) {
            throw new OAuth2Exception("OAuth2 authentication failed");
        }

        OAuth2UserInfo userInfo = authService.getUserFromToken(token);

        User user = userService.findBySocialConnection(userInfo.provider(), userInfo.providerId())
                .orElseGet(() -> userService.autoSaveAfterLogin(
                        userInfo.email(), userInfo.name(), userInfo.provider(), userInfo.providerId()
                ));

        cookieRepo.removeAuthorizationRequestCookies(request, response);

        String accessToken = jwtService.generateToken(user);
        cookieUtils.addCookie(response, "access_token", accessToken, jwtTokenExpiration / TO_SECONDS, true);
        response.sendRedirect(frontendUrl + "auth/callback");
    }
}
