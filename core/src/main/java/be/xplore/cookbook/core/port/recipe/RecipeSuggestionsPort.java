package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.recipe.Recipe;

public interface RecipeSuggestionsPort {
    SuggestedRecipeEnhancement enhanceRecipe(Recipe recipe);
}
