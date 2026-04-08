package cookbook.stage.backend.auth.application.oAuth2AuthService;

import cookbook.stage.backend.auth.application.OAuth2AuthService;
import cookbook.stage.backend.auth.application.RefreshTokenService;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.UserApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildAuthorizationUrlTests {

    @Mock private ClientRegistrationRepository registrationRepo;
    @Mock private UserApi userApi;
    @Mock private JwtService jwtService;
    @Mock private RestClient.Builder restClientBuilder;
    @Mock private RefreshTokenService refreshTokenService;

    @InjectMocks
    private OAuth2AuthService authService;

    @Test
    void buildAuthorizationUrl_shouldReturnUrlContainingClientIdAndRedirectUri() {
        // Arrange
        String redirectUri = "http://localhost:4200/callback";
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("google")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .clientId("my-client-id")
                .clientSecret("my-client-secret")
                .redirectUri(redirectUri)
                .scope("email", "profile")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        when(registrationRepo.findByRegistrationId("google")).thenReturn(registration);

        // Act
        String url = authService.buildAuthorizationUrl("google", redirectUri);

        // Assert
        assertThat(url)
                .contains("client_id=my-client-id")
                .contains("redirect_uri=" + redirectUri)
                .contains("response_type=code")
                .contains("https://accounts.google.com/o/oauth2/auth");
    }

    @Test
    void buildAuthorizationUrl_shouldContainAllScopes() {
        // Arrange
        String redirectUri = "http://localhost:4200/callback";
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("google")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .clientId("my-client-id")
                .clientSecret("my-client-secret")
                .redirectUri(redirectUri)
                .scope("email", "profile")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        when(registrationRepo.findByRegistrationId("google")).thenReturn(registration);

        // Act
        String url = authService.buildAuthorizationUrl("google", redirectUri);

        // Assert
        assertThat(url).contains("scope=").contains("email").contains("profile");
    }

    @Test
    void buildAuthorizationUrl_shouldThrow_whenProviderNotFound() {
        // Arrange
        when(registrationRepo.findByRegistrationId("unknown")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> authService.buildAuthorizationUrl("unknown", "http://localhost:4200/callback"))
                .isInstanceOf(Exception.class);
    }
}
