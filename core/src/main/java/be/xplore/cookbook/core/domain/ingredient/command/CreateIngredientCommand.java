package be.xplore.cookbook.core.domain.ingredient.command;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Unit;

import java.util.List;

public record CreateIngredientCommand(String name, Unit defaultUnit, List<Category> categories) {
}
