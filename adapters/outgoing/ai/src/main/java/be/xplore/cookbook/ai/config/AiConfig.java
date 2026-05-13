package be.xplore.cookbook.ai.config;

import be.xplore.cookbook.ai.RecipeAiService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public RecipeAiService recipeAiService(ChatModel chatModel, ToolProvider toolProvider) {
        return AiServices.builder(RecipeAiService.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .maxSequentialToolsInvocations(2)
                .build();
    }
}
