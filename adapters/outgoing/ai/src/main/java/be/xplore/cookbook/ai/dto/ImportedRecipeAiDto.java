package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.Macro;
import be.xplore.cookbook.core.port.recipe.ImportedIngredient;

import java.util.List;

public record ImportedRecipeAiDto(
        String title,
        String description,
        int durationInMinutes,
        int servings,
        List<String> steps,
        List<ImportedIngredient> ingredients,
        List<Macro> macros
) {
}
