package cookbook.stage.backend.recipe.api.dto;

public record IngredientDto(
        String name,
        double quantity,
        String unit
) {
}
