package be.xplore.cookbook.ai.config;

import be.xplore.cookbook.ai.RecipeImportAiService;
import be.xplore.cookbook.ai.component.WebFetchTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    RecipeImportAiService recipeImportAiService(ChatModel chatModel, WebFetchTool webFetchTool) {
        return AiServices.builder(RecipeImportAiService.class)
                .chatModel(chatModel)
                .tools(webFetchTool)
                .build();
    }
}
