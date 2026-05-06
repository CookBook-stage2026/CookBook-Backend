package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.RecipeInput;
import be.xplore.cookbook.ai.exception.AiConnectionException;
import be.xplore.cookbook.ai.exception.AiInvalidResponseException;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.port.recipe.RecipeSuggestionsPort;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Component
public class AiAdapter implements RecipeSuggestionsPort {

    private final RecipeSuggestionsAiService aiService;
    private final JsonMapper jsonMapper;

    public AiAdapter(RecipeSuggestionsAiService aiService, JsonMapper jsonMapper) {
        this.aiService = aiService;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public SuggestedRecipeEnhancement enhanceRecipe(Recipe recipe) {
        String recipeJson = serializeRecipe(recipe);
        try {
            return aiService.enhance(recipeJson);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw new AiConnectionException("AI service is unavailable", e);
            }
            throw new AiInvalidResponseException("AI returned an unexpected response", e);
        }
    }

    private String serializeRecipe(Recipe recipe) {
        try {
            return jsonMapper.writeValueAsString(RecipeInput.fromDomain(recipe));
        } catch (Exception e) {
            throw new AiInvalidResponseException("Failed to serialize recipe for LLM", e);
        }
    }
}
