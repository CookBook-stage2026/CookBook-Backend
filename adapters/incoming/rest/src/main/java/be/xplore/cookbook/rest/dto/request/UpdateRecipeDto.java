package be.xplore.cookbook.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateRecipeDto(
        @NotBlank String name,
        @NotBlank String description,
        int durationInMinutes,
        @NotEmpty List<@NotBlank String> steps,
        @NotEmpty List<@Valid NewRecipeIngredientDto> ingredients,
        @NotNull int servings
) {
}
