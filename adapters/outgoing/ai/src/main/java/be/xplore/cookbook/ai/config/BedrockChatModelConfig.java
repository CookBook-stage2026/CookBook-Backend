package be.xplore.cookbook.ai.config;

import dev.langchain4j.model.bedrock.BedrockChatModel;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Configuration
@Profile("deploy")
@EnableConfigurationProperties(BedrockProperties.class)
public class BedrockChatModelConfig {

    @Bean
    ChatModel selectedChatModel(BedrockProperties properties) {
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
