package be.xplore.cookbook.core.domain.ingredient.command;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;

public record CreateIngredientCommand(
        String name,
        Unit defaultUnit,
        List<Category> categories,
        UserId userId
) {
}
