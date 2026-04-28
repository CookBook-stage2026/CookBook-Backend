package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(RecipeId id, UserId userId);

    Page<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, UserPreferences preferences,
                                                   UserId userId, Pageable pageable);

    List<RecipeSummary> querySummaries(Pageable pageable, UserId userId, String query);

    long count();
}
