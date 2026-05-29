package be.xplore.cookbook.core.domain.ingredient.command;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;

public record UpdateIngredientCommand(IngredientId ingredientId, String name, Unit defaultUnit,
                                      List<Category> categories, UserId userId) {
}
