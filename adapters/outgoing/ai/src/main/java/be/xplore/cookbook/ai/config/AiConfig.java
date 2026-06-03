package be.xplore.cookbook.ai.config;

import be.xplore.cookbook.ai.RecipeAiService;
import be.xplore.cookbook.ai.ScheduleAiService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    private static final int MAXIMUM_TOOL_INVOCATIONS = 5;

    @Bean
    public RecipeAiService recipeAiService(ChatModel chatModel, ToolProvider toolProvider) {
        return AiServices.builder(RecipeAiService.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .maxSequentialToolsInvocations(MAXIMUM_TOOL_INVOCATIONS)
                .build();
    }

    @Bean
    public ScheduleAiService scheduleAiService(ChatModel chatModel) {
        return AiServices.builder(ScheduleAiService.class)
                .chatModel(chatModel)
                .build();
    }
}
