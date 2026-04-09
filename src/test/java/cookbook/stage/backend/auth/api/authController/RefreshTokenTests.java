package cookbook.stage.backend.auth.api.authController;

import cookbook.stage.backend.auth.api.dto.TokenRefreshRequest;
import cookbook.stage.backend.auth.api.dto.TokenRefreshResponse;
import cookbook.stage.backend.auth.shared.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RefreshTokenTests {

    private static final String DEFAULT_ACCESS_TOKEN = "access-token-abc";
    private static final String DEFAULT_REFRESH_TOKEN = "refresh-token-xyz";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private OAuth2AuthService authService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @Test
    void refreshToken_shouldReturnNewTokens_whenRefreshTokenIsValid() throws Exception {
        // Arrange
        TokenRefreshResponse refreshResponse = new TokenRefreshResponse(
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN
        );
        when(refreshTokenService.refreshAccessToken(DEFAULT_REFRESH_TOKEN)).thenReturn(refreshResponse);

        TokenRefreshRequest request = new TokenRefreshRequest(DEFAULT_REFRESH_TOKEN);

        // Act & Assert
        performRefreshToken(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(DEFAULT_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(DEFAULT_REFRESH_TOKEN));
    }

    private ResultActions performRefreshToken(TokenRefreshRequest request) throws Exception {
        return mockMvc.perform(post("/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print());
    }
}
