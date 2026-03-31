package cookbook.stage.backend.recipe.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class JpaRecipeIngredientId implements Serializable {
    @Column(nullable = false)
    private UUID recipeId;

    @Column(nullable = false)
    private UUID ingredientId;

    public JpaRecipeIngredientId(UUID recipeId, UUID ingredientId) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }

    protected JpaRecipeIngredientId() {
    }

    public UUID getRecipeId() {
        return recipeId;
    }

    public UUID getIngredientId() {
        return ingredientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof JpaRecipeIngredientId other)) {
            return false;
        }

        return Objects.equals(recipeId, other.recipeId) && Objects.equals(ingredientId, other.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, ingredientId);
    }
}
