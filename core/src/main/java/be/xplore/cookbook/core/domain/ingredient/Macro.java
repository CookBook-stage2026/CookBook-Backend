package be.xplore.cookbook.core.domain.ingredient;

public record Macro(
        MacroType type,
        double valuePerUnit
) {
    public Macro {
        if (type == null) {
            throw new IllegalArgumentException("Macro type cannot be null");
        }
        if (valuePerUnit < 0) {
            throw new IllegalArgumentException("Macro value per unit cannot be negative");
        }
    }
}
