package be.xplore.cookbook.ai.config;

import dev.langchain4j.model.bedrock.BedrockChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Configuration
@EnableConfigurationProperties({AiProperties.class, OllamaProperties.class, BedrockProperties.class})
public class LangChain4jConfig {

    @Bean
    ChatModel selectedChatModel(AiProperties aiProperties,
                                OllamaProperties ollamaProperties, BedrockProperties bedrockProperties) {
        return switch (aiProperties.provider()) {
            case "OLLAMA" -> createOllamaChatModel(ollamaProperties);
            case "BEDROCK" -> createBedrockChatModel(bedrockProperties);
            default -> throw new IllegalStateException("Unsupported AI provider: " + aiProperties.provider());
        };
    }

    private ChatModel createOllamaChatModel(OllamaProperties properties) {
        return OllamaChatModel.builder()
                .baseUrl(properties.baseUrl())
                .modelName(properties.modelName())
                .build();
    }

    private ChatModel createBedrockChatModel(BedrockProperties properties) {
        return BedrockChatModel.builder()
                .client(BedrockRuntimeClient.builder()
                        .region(Region.of(properties.region()))
                        .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.awsAccessKeyId(),
                                        properties.awsSecretAccessKey()
                                )
                        ))
                        .build())
                .modelId(properties.modelId())
                .build();
    }
}
