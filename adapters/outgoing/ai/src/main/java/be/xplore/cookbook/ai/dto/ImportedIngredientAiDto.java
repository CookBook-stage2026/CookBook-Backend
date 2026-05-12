package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.ingredient.Unit;

public record ImportedIngredientAiDto(String name, Unit unit, double quantity) {
}
