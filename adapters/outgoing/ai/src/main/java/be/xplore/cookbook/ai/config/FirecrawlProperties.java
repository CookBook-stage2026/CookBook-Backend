package be.xplore.cookbook.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile({"dev", "deploy"})
@ConfigurationProperties(prefix = "firecrawl")
public record FirecrawlProperties(
        String apiKey,
        String mcpUrl
) {
}

