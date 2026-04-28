package be.xplore.cookbook.security.strategy;

import be.xplore.cookbook.core.domain.exception.OAuth2Exception;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OAuth2UserExtractorRegistry {
    private final Map<String, OAuth2UserExtractorStrategy> strategies;

    public OAuth2UserExtractorRegistry(List<OAuth2UserExtractorStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(java.util.stream.Collectors.toMap(
                        OAuth2UserExtractorStrategy::getProvider,
                        strategy -> strategy
                ));
    }

    public OAuth2UserExtractorStrategy getStrategy(String provider) {
        OAuth2UserExtractorStrategy strategy = strategies.get(provider.toLowerCase());
        if (strategy == null) {
            throw new OAuth2Exception("Unknown OAuth2 provider: " + provider);
        }
        return strategy;
    }
}

