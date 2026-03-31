package cookbook.stage.backend.recipe.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateRecipeDto(
        @NotBlank String name,
        @NotBlank String description,
        int durationInMinutes,
        List<String> steps,
        List<RecipeIngredientDto> ingredients
) {
}
