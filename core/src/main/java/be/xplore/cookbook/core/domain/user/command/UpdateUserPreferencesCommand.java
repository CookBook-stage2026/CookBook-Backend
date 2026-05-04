package be.xplore.cookbook.core.domain.user.command;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;

public record UpdateUserPreferencesCommand(UserId userId,
                                    List<Category> excludedCategories, List<IngredientId> excludedIngredientIds) {
}
