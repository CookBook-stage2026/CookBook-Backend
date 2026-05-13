package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.ImportedRecipeAiDto;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface RecipeAiService {

    @SystemMessage(fromResource = "prompts/enhance-recipe.txt")
    @UserMessage("Enhance this recipe: {{it}}")
    SuggestedRecipeEnhancement enhanceRecipe(String recipeJson);

    @SystemMessage(fromResource = "prompts/suggest-recipe-for-day.txt")
    @UserMessage("Choose a fitting recipe for the requested day: {{it}}")
    SuggestedRecipeId suggestRecipeForDay(String weekScheduleJson);
    SuggestedRecipeEnhancement enhance(String recipeJson);

    @SystemMessage(fromResource = "prompts/import-recipe.txt")
    @UserMessage("Extract and translate the recipe from this text: {{it}}")
    ImportedRecipeAiDto importFromUrl(String url);
}
