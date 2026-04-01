package cookbook.stage.backend.recipe.domain;

public record Ingredient(String name, double quantity, String unit) {
    public Ingredient {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient must have a name");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ingredient quantity must be greater than 0");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Ingredient must have a unit of measurement");
        }
    }
}
