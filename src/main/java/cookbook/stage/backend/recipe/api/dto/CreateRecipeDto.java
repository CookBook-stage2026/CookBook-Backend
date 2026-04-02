package cookbook.stage.backend.recipe.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateRecipeDto(
        @NotBlank String name,
        @NotBlank String description,
        int durationInMinutes,
        @NotEmpty List<String> steps,
        @NotEmpty List<IngredientDto> ingredients
) {
}
