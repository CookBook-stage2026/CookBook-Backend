package cookbook.stage.backend.auth.application;

import cookbook.stage.backend.auth.api.dto.TokenRefreshResponse;
import cookbook.stage.backend.auth.domain.RefreshToken;
import cookbook.stage.backend.auth.domain.RefreshTokenRepository;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.UserApi;
import cookbook.stage.backend.user.shared.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserApi userApi;
    private final JwtService jwtService;

    @Value("${app.jwt.remember-me-expiration-ms}")
    private long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserApi userApi,
                               JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userApi = userApi;
        this.jwtService = jwtService;
    }

    public String createRefreshToken(UserId userId) {
        // 1 session per user
        refreshTokenRepository.deleteByUserId(userId);

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);

        RefreshToken refreshToken = new RefreshToken(token, userId, expiryDate);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public TokenRefreshResponse refreshAccessToken(String requestToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new AuthorizationDeniedException("Refresh token is not in database!"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken.token());
            throw new AuthorizationDeniedException("Refresh token was expired. Please make a new signin request");
        }

        return userApi.findById(refreshToken.userId())
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    return new TokenRefreshResponse(newAccessToken, requestToken);
                })
                .orElseThrow(() -> new AuthorizationDeniedException("User associated with refresh token not found!"));
    }
}
