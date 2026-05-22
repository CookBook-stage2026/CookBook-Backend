package be.xplore.cookbook.core.domain.ingredient.command;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;

public record SearchIngredientsQuery(String name, List<IngredientId> excludedIds, Paging paging, UserId userId) {
}
