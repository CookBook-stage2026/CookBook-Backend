package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.recipe.Macro;
import be.xplore.cookbook.core.domain.recipe.Recipe;

import java.util.List;

public interface RecipeSuggestionsPort {
    SuggestedRecipeEnhancement enhanceRecipe(Recipe recipe);
    List<Macro> generateMacros(Recipe recipe);
}
