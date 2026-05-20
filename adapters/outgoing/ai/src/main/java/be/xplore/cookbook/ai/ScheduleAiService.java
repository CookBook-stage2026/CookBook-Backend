package be.xplore.cookbook.ai;

import be.xplore.cookbook.core.port.recipe.SuggestedRecipeId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ScheduleAiService {
    @SystemMessage(fromResource = "prompts/suggest-recipe-for-day.txt")
    @UserMessage("Choose a fitting recipe for the requested day: {{it}}")
    SuggestedRecipeId suggestRecipeForDay(String weekScheduleJson);
}
