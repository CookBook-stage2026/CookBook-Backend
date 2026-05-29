package be.xplore.cookbook.core.domain.ingredient.command;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;

public record DeleteIngredientCommand(IngredientId ingredientId, UserId userId) {
}
