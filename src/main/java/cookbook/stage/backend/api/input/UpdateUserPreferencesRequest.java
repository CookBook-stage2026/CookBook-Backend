package cookbook.stage.backend.api.input;

import cookbook.stage.backend.domain.ingredient.Category;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateUserPreferencesRequest(
        @NotNull List<Category> excludedCategories,
        @NotNull List<UUID> excludedIngredientIds
) {
}
