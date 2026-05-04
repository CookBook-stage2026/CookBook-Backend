package be.xplore.cookbook.core.ai;

import be.xplore.cookbook.core.domain.recipe.Recipe;

public interface AiPort {
    EnhancedRecipeSuggestion enhanceRecipe(Recipe recipe);
}
