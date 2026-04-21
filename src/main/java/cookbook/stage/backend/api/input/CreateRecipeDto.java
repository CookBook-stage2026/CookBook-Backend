package cookbook.stage.backend.api.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateRecipeDto(
        @NotBlank String name,
        @NotBlank String description,
        int durationInMinutes,
        @NotEmpty List<@NotBlank String> steps,
        @NotEmpty List<@Valid CreateRecipeIngredientDto> ingredients,
        @NotNull int servings
) {
}
