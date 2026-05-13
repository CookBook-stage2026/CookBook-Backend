package be.xplore.cookbook.ai.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@Profile("dev")
@EnableConfigurationProperties(OllamaProperties.class)
public class OllamaChatModelConfig {
    @Bean
    ChatModel selectedChatModel(OllamaProperties properties) {
        return OllamaChatModel.builder()
                .baseUrl(properties.baseUrl())
                .modelName(properties.modelName())
                .timeout(Duration.ofMinutes(properties.maxResponseTimeInMinutes()))
                .build();
    }
}
