package be.xplore.cookbook.rest.dto.ingredient.request;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.MacroType;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateIngredientDto(
        @NotBlank String name,
        @NotNull Unit defaultUnit,
        @NotEmpty List<Category> categories,
        @NotEmpty List<MacroRequest> macros
) {
    public record MacroRequest(
            @NotNull MacroType type,
            @NotNull double valuePerUnit
    ) {
    }
}
