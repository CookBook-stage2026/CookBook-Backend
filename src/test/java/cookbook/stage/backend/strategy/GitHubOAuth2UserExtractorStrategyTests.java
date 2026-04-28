package cookbook.stage.backend.strategy;

import be.xplore.cookbook.core.domain.auth.OAuth2UserInfo;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubOAuth2UserExtractorStrategyTests {
    private static final String PROVIDER_GITHUB = "github";
    private static final int GITHUB_ID_INT = 12345;
    private static final String GITHUB_ID_STR = "12345";
    private static final String VALID_EMAIL = "user@github.com";
    private static final String FALLBACK_EMAIL = "12345@github.local";
    private static final String BLANK_EMAIL = "   ";
    private static final String FULL_NAME = "Jane Doe";
    private static final String LOGIN_NAME = "janedoe";
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOGIN = "login";

    private final GitHubOAuth2UserExtractorStrategy strategy = new GitHubOAuth2UserExtractorStrategy();

    @Test
    void extractUserInfo_WithEmailAndName_ReturnsOAuth2UserInfo() {
        Map<String, Object> attributes = Map.of(
                KEY_ID, GITHUB_ID_INT,
                KEY_EMAIL, VALID_EMAIL,
                KEY_NAME, FULL_NAME,
                KEY_LOGIN, LOGIN_NAME
        );

        OAuth2UserInfo userInfo = strategy.extractUserInfo(attributes);

        assertThat(userInfo)
                .returns(PROVIDER_GITHUB, OAuth2UserInfo::provider)
                .returns(GITHUB_ID_STR, OAuth2UserInfo::providerId)
                .returns(VALID_EMAIL, OAuth2UserInfo::email)
                .returns(FULL_NAME, OAuth2UserInfo::name);
    }

    @Test
    void extractUserInfo_WithoutEmail_UsesFallbackEmail() {
        Map<String, Object> attributes = Map.of(
                KEY_ID, GITHUB_ID_INT,
                KEY_NAME, FULL_NAME,
                KEY_LOGIN, LOGIN_NAME
        );

        OAuth2UserInfo userInfo = strategy.extractUserInfo(attributes);

        assertThat(userInfo)
                .returns(FALLBACK_EMAIL, OAuth2UserInfo::email);
    }

    @Test
    void extractUserInfo_WithBlankEmail_UsesFallbackEmail() {
        Map<String, Object> attributes = Map.of(
                KEY_ID, GITHUB_ID_INT,
                KEY_EMAIL, BLANK_EMAIL,
                KEY_NAME, FULL_NAME,
                KEY_LOGIN, LOGIN_NAME
        );

        OAuth2UserInfo userInfo = strategy.extractUserInfo(attributes);

        assertThat(userInfo)
                .returns(FALLBACK_EMAIL, OAuth2UserInfo::email);
    }

    @Test
    void extractUserInfo_WithoutNameButWithLogin_UsesLoginName() {
        Map<String, Object> attributes = Map.of(
                KEY_ID, GITHUB_ID_INT,
                KEY_EMAIL, VALID_EMAIL,
                KEY_LOGIN, LOGIN_NAME
        );

        OAuth2UserInfo userInfo = strategy.extractUserInfo(attributes);

        assertThat(userInfo)
                .returns(LOGIN_NAME, OAuth2UserInfo::name);
    }
}

