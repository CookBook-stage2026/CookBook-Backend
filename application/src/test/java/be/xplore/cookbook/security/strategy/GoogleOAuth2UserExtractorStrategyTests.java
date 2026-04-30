package be.xplore.cookbook.security.strategy;

import be.xplore.cookbook.core.domain.auth.OAuth2UserInfo;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleOAuth2UserExtractorStrategyTests {
    private static final String PROVIDER_GOOGLE = "google";
    private static final String SUB_ID = "google-123";
    private static final String EMAIL = "user@gmail.com";
    private static final String NAME = "John Doe";
    private static final String KEY_SUB = "sub";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";

    private final GoogleOAuth2UserExtractorStrategy strategy = new GoogleOAuth2UserExtractorStrategy();

    @Test
    void extractUserInfo_ValidAttributes_ReturnsOAuth2UserInfo() {
        Map<String, Object> attributes = Map.of(
                KEY_SUB, SUB_ID,
                KEY_EMAIL, EMAIL,
                KEY_NAME, NAME
        );

        OAuth2UserInfo userInfo = strategy.extractUserInfo(attributes);

        assertThat(userInfo)
                .returns(PROVIDER_GOOGLE, OAuth2UserInfo::provider)
                .returns(SUB_ID, OAuth2UserInfo::providerId)
                .returns(EMAIL, OAuth2UserInfo::email)
                .returns(NAME, OAuth2UserInfo::name);
    }
}

