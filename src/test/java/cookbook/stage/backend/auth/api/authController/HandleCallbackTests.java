package cookbook.stage.backend.auth.api.authController;

import cookbook.stage.backend.auth.api.dto.AuthResponse;
import cookbook.stage.backend.auth.api.dto.CallbackRequest;
import cookbook.stage.backend.auth.application.OAuth2AuthService;
import cookbook.stage.backend.auth.application.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.json.JsonMapper;

import javax.management.ServiceNotFoundException;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class HandleCallbackTests {

    private static final String DEFAULT_PROVIDER = "google";
    private static final String DEFAULT_REDIRECT_URI = "http://localhost:3000/callback";
    private static final String DEFAULT_CODE = "auth-code-123";
    private static final String DEFAULT_ACCESS_TOKEN = "access-token-abc";
    private static final String DEFAULT_REFRESH_TOKEN = "refresh-token-xyz";
    private static final String DEFAULT_EMAIL = "user@example.com";
    private static final String DEFAULT_DISPLAY_NAME = "Test User";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private OAuth2AuthService authService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @Test
    void handleCallback_shouldReturnAuthResponse_whenCallbackIsValid() throws Exception {
        // Arrange
        AuthResponse authResponse = new AuthResponse(
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_EMAIL,
                DEFAULT_DISPLAY_NAME
        );
        when(authService.handleCallback(anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(authResponse);

        CallbackRequest request = new CallbackRequest(DEFAULT_CODE, DEFAULT_REDIRECT_URI, false);

        // Act & Assert
        performHandleCallback(DEFAULT_PROVIDER, request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(DEFAULT_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(DEFAULT_REFRESH_TOKEN))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME));
    }

    @Test
    void handleCallback_shouldPropagateException_whenServiceThrows() throws Exception {
        // Arrange
        when(authService.handleCallback(anyString(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new ServiceNotFoundException("Provider not found"));

        CallbackRequest request = new CallbackRequest(DEFAULT_CODE, DEFAULT_REDIRECT_URI, false);

        // Act & Assert
        performHandleCallback(DEFAULT_PROVIDER, request)
                .andExpect(status().is5xxServerError());
    }

    private ResultActions performHandleCallback(String provider, CallbackRequest request) throws Exception {
        return mockMvc.perform(post("/auth/{provider}/callback", provider)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print());
    }
}
