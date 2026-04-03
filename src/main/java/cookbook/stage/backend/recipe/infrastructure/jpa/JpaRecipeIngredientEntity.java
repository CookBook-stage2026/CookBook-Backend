package cookbook.stage.backend.recipe.infrastructure.jpa;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "recipe_ingredients")
public class JpaRecipeIngredientEntity {
    @EmbeddedId
    private JpaRecipeIngredientId id;

    @Column(nullable = false)
    private double baseQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private JpaRecipeEntity recipe;

    @Column(name = "ingredient_id", nullable = false, insertable = false, updatable = false)
    private UUID ingredientId;

    public JpaRecipeIngredientEntity(JpaRecipeEntity recipe, UUID ingredientId, double baseQuantity) {
        this.id = new JpaRecipeIngredientId(recipe.getId(), ingredientId);
        this.baseQuantity = baseQuantity;
        this.recipe = recipe;
        this.ingredientId = ingredientId;
    }

    protected JpaRecipeIngredientEntity() {
    }

    public static JpaRecipeIngredientEntity fromDomain(RecipeIngredient recipeIngredient, JpaRecipeEntity recipe) {
        return new JpaRecipeIngredientEntity(
                recipe,
                recipeIngredient.ingredientId().id(),
                recipeIngredient.baseQuantity()
        );
    }

    public RecipeIngredient toDomain() {
        return new RecipeIngredient(
                new RecipeId(id.getRecipeId()),
                new IngredientId(id.getIngredientId()),
                baseQuantity
        );
    }
}
