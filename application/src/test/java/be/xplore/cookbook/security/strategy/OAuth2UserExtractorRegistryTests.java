package be.xplore.cookbook.security.strategy;

import be.xplore.cookbook.security.exception.OAuth2Exception;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth2UserExtractorRegistryTests {
    private final OAuth2UserExtractorRegistry registry = new OAuth2UserExtractorRegistry(
            List.of(new GitHubOAuth2UserExtractorStrategy(), new GoogleOAuth2UserExtractorStrategy())
    );

    @Test
    void getStrategy_UnknownProvider_ThrowsOAuth2Exception() {
        assertThatThrownBy(() -> registry.getStrategy("yahoo"))
                .isInstanceOf(OAuth2Exception.class)
                .hasMessageContaining("Unknown OAuth2 provider: yahoo");
    }
}
