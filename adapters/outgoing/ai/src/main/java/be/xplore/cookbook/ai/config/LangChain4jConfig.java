package be.xplore.cookbook.ai.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OllamaProperties.class)
public class LangChain4jConfig {
    @Bean
    ChatModel chatModel(OllamaProperties properties) {
        return OllamaChatModel.builder()
                .baseUrl(properties.baseUrl())
                .modelName(properties.modelName())
                .build();
    }
}
