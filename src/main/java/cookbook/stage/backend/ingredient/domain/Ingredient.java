package cookbook.stage.backend.ingredient.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.Optional;

@AggregateRoot
public record Ingredient(@Identity IngredientId id, String name, String description, Optional<Unit> unit) {
    public Ingredient {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or blank!");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Ingredient description cannot be null or blank!");
        }
    }

    public static Ingredient createIngredient(String name, String description, Unit unit) {
        return new Ingredient(IngredientId.create(), name, description, Optional.of(unit));
    }

    public static Ingredient createIngredient(String name, String description) {
        return new Ingredient(IngredientId.create(), name, description, Optional.empty());
    }
}
