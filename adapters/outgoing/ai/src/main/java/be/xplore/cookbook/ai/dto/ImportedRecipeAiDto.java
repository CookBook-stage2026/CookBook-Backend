package be.xplore.cookbook.ai.dto;

import java.util.List;

public record ImportedRecipeAiDto(
        String title,
        String description,
        int durationInMinutes,
        int servings,
        List<String> steps,
        List<ImportedIngredientAiDto> ingredients
) {
}
