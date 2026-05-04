package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(RecipeId id, UserId userId);

    PagedResult<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, UserPreferences preferences,
                                                          User user, Paging pageable);

    List<RecipeSummary> querySummaries(Paging pageable, User user, String query);

    long count();
}
