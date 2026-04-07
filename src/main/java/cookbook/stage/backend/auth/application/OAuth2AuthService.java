package cookbook.stage.backend.auth.application;

import cookbook.stage.backend.auth.api.AuthResponse;
import cookbook.stage.backend.shared.infrastructure.security.JwtService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.management.ServiceNotFoundException;
import java.util.Map;

@Service
public class OAuth2AuthService {
    private final ClientRegistrationRepository registrationRepo;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserApi userApi;
    private final JwtService jwtService;
    private final RestClient restClient;

    public OAuth2AuthService(ClientRegistrationRepository registrationRepo,
                             OAuth2AuthorizedClientService authorizedClientService,
                             UserApi userApi,
                             JwtService jwtService) {
        this.registrationRepo = registrationRepo;
        this.authorizedClientService = authorizedClientService;
        this.userApi = userApi;
        this.jwtService = jwtService;
        this.restClient = RestClient.create();
    }

    public String buildAuthorizationUrl(String provider, String redirectUri) {
        ClientRegistration registration = registrationRepo.findByRegistrationId(provider);
        return UriComponentsBuilder
                .fromUriString(registration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", registration.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", registration.getScopes()))
                .build()
                .toUriString();
    }

    public AuthResponse handleCallback(String provider,
                                       String code,
                                       String redirectUri) throws ServiceNotFoundException {
        ClientRegistration registration = registrationRepo.findByRegistrationId(provider);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("client_id", registration.getClientId());
        params.add("client_secret", registration.getClientSecret());

        Map<String, Object> tokenResponse = restClient.post()
                .uri(registration.getProviderDetails().getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (tokenResponse == null) {
            throw new ServiceNotFoundException("Tokenresponse is null. Provider: " + provider);
        }
        String accessToken = (String) tokenResponse.getOrDefault("access_token", "");

        // 2. Fetch user info from the provider
        Map<String, Object> userAttributes = restClient.get()
                .uri(registration.getProviderDetails().getUserInfoEndpoint().getUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        // 3. Normalize across providers
        OAuth2UserInfo userInfo = OAuth2UserInfo.from(provider, userAttributes);

        // 4. Find or create user
        User user = userApi
                .findBySocialConnection(userInfo.provider(), userInfo.providerId())
                .orElseGet(() -> userApi.autoSaveAfterLogin(userInfo.email(),
                        userInfo.name(),
                        userInfo.provider(),
                        userInfo.providerId()));

        // 5. Issue your own JWT
        return new AuthResponse(jwtService.generateToken(user), user.getEmail(), user.getDisplayName());
    }
}
