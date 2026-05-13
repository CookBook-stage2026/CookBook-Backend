package be.xplore.cookbook.ai;

import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface RecipeSuggestionsAiService {

    @SystemMessage(fromResource = "prompts/enhance-recipe.txt")
    @UserMessage("Enhance this recipe: {{it}}")
    SuggestedRecipeEnhancement enhanceRecipe(String recipeJson);

    @SystemMessage(fromResource = "prompts/suggest-recipe-for-day.txt")
    @UserMessage("Choose a fitting recipe for the requested day: {{it}}")
    SuggestedRecipeId suggestRecipeForDay(String weekScheduleJson);
}
