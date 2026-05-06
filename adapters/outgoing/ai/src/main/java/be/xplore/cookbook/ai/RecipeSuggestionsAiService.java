package be.xplore.cookbook.ai;

import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface RecipeSuggestionsAiService {

    @SystemMessage(fromResource = "prompts/enhance-recipe.txt")
    @UserMessage("Enhance this recipe: {{it}}")
    SuggestedRecipeEnhancement enhance(String recipeJson);
}
