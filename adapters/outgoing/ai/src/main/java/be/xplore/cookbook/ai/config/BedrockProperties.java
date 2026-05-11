package be.xplore.cookbook.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bedrock")
public record BedrockProperties(
        String awsAccessKeyId,
        String awsSecretAccessKey,
        String modelId,
        String region
) {
}
