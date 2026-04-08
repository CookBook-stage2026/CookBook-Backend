package cookbook.stage.backend.auth.api.authController;

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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class GetAuthUrlTests {

    private static final String DEFAULT_PROVIDER = "google";
    private static final String DEFAULT_REDIRECT_URI = "http://localhost:3000/callback";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OAuth2AuthService authService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @Test
    void getAuthUrl_shouldReturnUrl_whenProviderAndRedirectUriAreValid() throws Exception {
        // Arrange
        String expectedUrl = "https://accounts.google.com/o/oauth2/auth?redirect_uri=" + DEFAULT_REDIRECT_URI;
        when(authService.buildAuthorizationUrl(DEFAULT_PROVIDER, DEFAULT_REDIRECT_URI)).thenReturn(expectedUrl);

        // Act & Assert
        performGetAuthUrl(DEFAULT_PROVIDER, DEFAULT_REDIRECT_URI)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url").value(expectedUrl));
    }

    private ResultActions performGetAuthUrl(String provider, String redirectUri) throws Exception {
        return mockMvc.perform(get("/auth/{provider}/url", provider)
                        .param("redirectUri", redirectUri))
                .andDo(print());
    }
}
