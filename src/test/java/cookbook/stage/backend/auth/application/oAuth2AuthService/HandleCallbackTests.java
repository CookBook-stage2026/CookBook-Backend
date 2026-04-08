package cookbook.stage.backend.auth.application.oAuth2AuthService;

import cookbook.stage.backend.auth.api.dto.AuthResponse;
import cookbook.stage.backend.auth.application.OAuth2AuthService;
import cookbook.stage.backend.auth.application.RefreshTokenService;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import cookbook.stage.backend.user.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import javax.management.ServiceNotFoundException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

@ExtendWith(MockitoExtension.class)
class HandleCallbackTests {
    @Mock private ClientRegistrationRepository registrationRepo;
    @Mock private UserApi userApi;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;

    private OAuth2AuthService authService;
    private MockRestServiceServer mockServer;
    private ClientRegistration googleRegistration;

    @BeforeEach
    void setUp() {
        googleRegistration = ClientRegistration.withRegistrationId("google")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientId("client-id")
                .clientSecret("client-secret")
                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                .scope("email", "profile")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        when(registrationRepo.findByRegistrationId("google")).thenReturn(googleRegistration);

        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        authService = new OAuth2AuthService(registrationRepo, userApi, jwtService, builder, refreshTokenService);
    }

    @Test
    void handleCallback_shouldReturnAuthResponse_whenUserExists_andRememberMeFalse() throws Exception {
        // Arrange
        mockTokenExchange("mock-token");
        mockUserInfoRequest("mock-token", "123", "existing@example.com", "Existing User");

        User existingUser = mock(User.class);
        when(existingUser.getEmail()).thenReturn("existing@example.com");
        when(existingUser.getDisplayName()).thenReturn("Existing User");
        when(userApi.findBySocialConnection("google", "123")).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(existingUser)).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.
                handleCallback("google", "auth-code", "http://localhost:4200/callback", false);

        // Assert
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.refreshToken()).isNull();
        verify(userApi, never()).autoSaveAfterLogin(anyString(), anyString(), anyString(), anyString());
        mockServer.verify();
    }

    @Test
    void handleCallback_shouldAutoSaveNewUser_andReturnAuthResponse() throws Exception {
        // Arrange
        mockTokenExchange("mock-token");
        mockUserInfoRequest("mock-token", "new-123", "new@example.com", "New User");

        when(userApi.findBySocialConnection("google", "new-123")).thenReturn(Optional.empty());
        User newUser = mock(User.class);
        when(newUser.getEmail()).thenReturn("new@example.com");
        when(newUser.getDisplayName()).thenReturn("New User");
        when(userApi.autoSaveAfterLogin("new@example.com", "New User", "google", "new-123")).thenReturn(newUser);
        when(jwtService.generateToken(newUser)).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService
                .handleCallback("google", "auth-code", "http://localhost:4200/callback", false);

        // Assert
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        verify(userApi).autoSaveAfterLogin("new@example.com", "New User", "google", "new-123");
        mockServer.verify();
    }

    @Test
    void handleCallback_shouldCreateRefreshToken_whenRememberMeTrue() throws Exception {
        // Arrange
        mockTokenExchange("mock-token");
        mockUserInfoRequest("mock-token", "123", "user@example.com", "User");

        User user = mock(User.class);
        UserId userId = new UserId(UUID.randomUUID());
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getDisplayName()).thenReturn("User");
        when(userApi.findBySocialConnection("google", "123")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(userId)).thenReturn("refresh-token");

        // Act
        AuthResponse response = authService
                .handleCallback("google", "auth-code", "http://localhost:4200/callback", true);

        // Assert
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenService).createRefreshToken(userId);
        mockServer.verify();
    }

    @Test
    void handleCallback_shouldThrowServiceNotFoundException_whenTokenResponseIsNull() {
        // Arrange
        mockServer.expect(requestTo(googleRegistration.getProviderDetails().getTokenUri()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // Act & Assert
        assertThatThrownBy(() -> authService.handleCallback("google", "code", "http://localhost:4200/callback", false))
                .isInstanceOf(ServiceNotFoundException.class)
                .hasMessageContaining("Tokenresponse is null");

        mockServer.verify();
    }

    @Test
    void handleCallback_shouldPropagateException_whenUserInfoFails() {
        // Arrange
        mockTokenExchange("mock-token");
        mockServer.expect(requestTo(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer mock-token"))
                .andRespond(withUnauthorizedRequest());

        // Act & Assert
        assertThatThrownBy(() -> authService.handleCallback("google", "code", "http://localhost:4200/callback", false))
                .isInstanceOf(HttpClientErrorException.class);
    }

    private void mockTokenExchange(String accessToken) {
        mockServer.expect(requestTo(googleRegistration.getProviderDetails().getTokenUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andRespond(withSuccess("{\"access_token\":\"" + accessToken + "\"}", MediaType.APPLICATION_JSON));
    }

    private void mockUserInfoRequest(String accessToken, String sub, String email, String name) {
        mockServer.expect(requestTo(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + accessToken))
                .andRespond(withSuccess(
                        String.format("{\"sub\":\"%s\",\"email\":\"%s\",\"name\":\"%s\"}", sub, email, name),
                        MediaType.APPLICATION_JSON));
    }
}
