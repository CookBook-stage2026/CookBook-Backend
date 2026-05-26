package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.ImportedRecipeAiDto;
import be.xplore.cookbook.ai.dto.SuggestedMacros;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface RecipeAiService {

    @SystemMessage(fromResource = "prompts/enhance-recipe.txt")
    @UserMessage("Enhance this recipe: {{it}}")
    SuggestedRecipeEnhancement enhanceRecipe(String recipeJson);

    @SystemMessage(fromResource = "prompts/import-recipe.txt")
    @UserMessage("Extract and translate the recipe from this text: {{it}}")
    ImportedRecipeAiDto importFromUrl(String url);

    @SystemMessage(fromResource = "prompts/generate-recipe-macros.txt")
    @UserMessage("Generate macros for this recipe: {{it}}")
    SuggestedMacros generateMacros(String recipeJson);
}
