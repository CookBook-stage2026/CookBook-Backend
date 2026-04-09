package cookbook.stage.backend.auth.api.authController;

import cookbook.stage.backend.auth.domain.RefreshToken;
import cookbook.stage.backend.auth.domain.RefreshTokenRepository;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RefreshTokenTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserApi userApi;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    private static final int EXPIRE_TIME = 3600;

    @Test
    void refreshToken_ValidCookie_ReturnsTokenRefreshResponse() throws Exception {
        // Arrange
        User user = userApi.autoSaveAfterLogin("refresh@example.com", "Refresh User", "google", "refresh-123");

        String plainToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                plainToken,
                user.getId(),
                Instant.now().plusSeconds(EXPIRE_TIME)
        );
        refreshTokenRepository.save(refreshToken);

        Cookie cookie = new Cookie("refresh_token", plainToken);

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").value(plainToken));
    }

    @Test
    void refreshToken_MissingCookie_ReturnsUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_BlankCookie_ReturnsUnauthorized() throws Exception {
        // Arrange
        Cookie emptyCookie = new Cookie("refresh_token", "");

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                        .cookie(emptyCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
