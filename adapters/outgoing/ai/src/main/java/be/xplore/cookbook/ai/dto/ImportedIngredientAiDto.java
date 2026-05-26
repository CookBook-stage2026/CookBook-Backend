package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Unit;

import java.util.List;

public record ImportedIngredientAiDto(String name, Unit unit, double quantity, List<Category> categories) {
}
