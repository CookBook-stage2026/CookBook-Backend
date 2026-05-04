package be.xplore.cookbook.core.domain.ingredient.command;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;

import java.util.List;

public record SearchIngredientsQuery(String name, List<IngredientId> excludedIds, Paging paging) {
}
