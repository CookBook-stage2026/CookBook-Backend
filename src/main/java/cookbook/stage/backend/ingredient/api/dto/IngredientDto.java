package cookbook.stage.backend.ingredient.api.dto;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.Unit;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;
import java.util.UUID;

public record IngredientDto(
        @Nullable Optional<UUID> id,
        @NotBlank String name,
        @NotBlank String description,
        @Nullable Optional<Unit> unit
) {
    public static IngredientDto fromDomain(Ingredient ingredient) {
        return new IngredientDto(Optional.ofNullable(ingredient.id().id()),
                ingredient.name(), ingredient.description(), ingredient.unit());
    }
}
