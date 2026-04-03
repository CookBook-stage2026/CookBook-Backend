package cookbook.stage.backend.recipe.infrastructure.jpa;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "recipe_ingredients")
public class JpaRecipeIngredientEntity {

    @EmbeddedId
    private JpaRecipeIngredientId id;

    @Column(name = "base_quantity", nullable = false)
    private double baseQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private JpaRecipeEntity recipe;

    protected JpaRecipeIngredientEntity() {}

    public JpaRecipeIngredientEntity(JpaRecipeEntity recipe, RecipeIngredient recipeIngredient) {
        this.id = new JpaRecipeIngredientId(
                recipeIngredient.recipeId().id(),
                recipeIngredient.ingredientId().id()
        );
        this.recipe = recipe;
        this.baseQuantity = recipeIngredient.baseQuantity();
    }

    public RecipeIngredient toDomain() {
        return new RecipeIngredient(
                new RecipeId(id.getRecipeId()),
                new IngredientId(id.getIngredientId()),
                baseQuantity
        );
    }

    public void setRecipe(JpaRecipeEntity recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaRecipeIngredientEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}