package be.xplore.cookbook.config;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.rest.dto.request.RecipeSearchRequest;
import be.xplore.cookbook.security.CookieAuthorizationRequestRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(OAuth2LoginEndToEndTest.TestClientRegistrationConfig.class)
class OAuth2LoginEndToEndTest {

    private static final String PROVIDER_ID = "google";
    private static final String MOCK_AUTH_CODE = "mock-auth-code";
    private static final String MOCK_ACCESS_TOKEN = "mock-access-token";
    private static final String MOCK_USER_SUB = "123456789";
    private static final String MOCK_USER_EMAIL = "testuser@gmail.com";
    private static final String MOCK_USER_NAME = "Test User";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private static WireMockServer wireMockServer;
    private static String mockProviderBaseUrl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper mapper;

    @Autowired
    private UserService userApi;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
        mockProviderBaseUrl = wireMockServer.baseUrl();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void stubOAuth2Endpoints() {
        // Token
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/oauth2/token"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "access_token": "%s",
                                    "token_type": "Bearer",
                                    "expires_in": 3600
                                }
                                """.formatted(MOCK_ACCESS_TOKEN))));

        // UserInfo
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/oauth2/userinfo"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "sub": "%s",
                                    "email": "%s",
                                    "name": "%s",
                                    "email_verified": true
                                }
                                """.formatted(MOCK_USER_SUB, MOCK_USER_EMAIL, MOCK_USER_NAME))));
    }

    @Test
    void completeOAuth2LoginFlow_NewUser_Success() throws Exception {
        MvcResult initResult = mockMvc.perform(get("/oauth2/authorization/" + PROVIDER_ID))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = initResult.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).isNotNull().startsWith(mockProviderBaseUrl + "/oauth2/auth");

        Cookie stateCookie = initResult.getResponse().getCookie(
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        assertThat(stateCookie).isNotNull();
        String stateCookieValue = stateCookie.getValue();

        String encodedState = UriComponentsBuilder.fromUriString(location)
                .build()
                .getQueryParams()
                .getFirst("state");

        assertThat(encodedState).isNotBlank();

        String state = URLDecoder.decode(encodedState, StandardCharsets.UTF_8);
        assertThat(state).isNotBlank();

        MvcResult callbackResult = mockMvc.perform(get("/login/oauth2/code/" + PROVIDER_ID)
                        .param("state", state)
                        .param("code", MOCK_AUTH_CODE)
                        .cookie(new Cookie(
                                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                                stateCookieValue)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:4200/auth/callback"))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().httpOnly("access_token", true))
                .andReturn();

        WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/oauth2/token")));
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/oauth2/userinfo")));
        Cookie clearedStateCookie = callbackResult.getResponse().getCookie(
                CookieAuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        assertThat(clearedStateCookie).isNotNull();
        assertThat(clearedStateCookie.getMaxAge()).isZero();

        Optional<User> createdUser = userApi.findBySocialConnection(PROVIDER_ID, MOCK_USER_SUB);
        assertThat(createdUser).isPresent()
                .hasValueSatisfying(user -> assertThat(user)
                        .returns(MOCK_USER_EMAIL, User::email)
                        .returns(MOCK_USER_NAME, User::displayName)
                );

        Optional<UserPreferences> preferences = userPreferenceRepository.findPreferences(createdUser.get());
        assertThat(preferences).isPresent();

        Cookie accessTokenCookie = callbackResult.getResponse().getCookie("access_token");
        assertThat(accessTokenCookie).isNotNull();

        var requestBuilder = post("/api/recipes/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(
                        new RecipeSearchRequest(List.of(), true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)))
                .cookie(accessTokenCookie);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class TestClientRegistrationConfig {
        @Bean
        @Primary
        public ClientRegistrationRepository clientRegistrationRepository() {
            ClientRegistration google = ClientRegistration.withRegistrationId(PROVIDER_ID)
                    .clientId("test-client-id")
                    .clientSecret("test-client-secret")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("profile", "email")
                    .authorizationUri(mockProviderBaseUrl + "/oauth2/auth")
                    .tokenUri(mockProviderBaseUrl + "/oauth2/token")
                    .userInfoUri(mockProviderBaseUrl + "/oauth2/userinfo")
                    .userNameAttributeName(IdTokenClaimNames.SUB)
                    .clientName("Google")
                    .build();
            return new InMemoryClientRegistrationRepository(google);
        }
    }
}
