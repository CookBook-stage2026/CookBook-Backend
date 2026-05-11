package be.xplore.cookbook.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public record AiProperties(
        String provider
) {
    public enum Provider {
        OLLAMA, BEDROCK
    }
}
