package be.xplore.cookbook.rest.dto.response;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;

import java.util.List;
import java.util.UUID;

public record IngredientDto(
        UUID id,
        String name,
        Unit unit,
        List<Category> categories
) {
    public static IngredientDto fromDomain(Ingredient ingredient) {
        return new IngredientDto(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.unit(),
                ingredient.categories()
        );
    }
}
