package cookbook.stage.backend.recipe.infrastructure.jpa.recipe;

import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import cookbook.stage.backend.recipe.domain.recipe.RecipeIngredient;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

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

    protected JpaRecipeIngredientEntity() {
    }

    public JpaRecipeIngredientEntity(JpaRecipeEntity recipe, RecipeIngredient recipeIngredient) {
        this.id = new JpaRecipeIngredientId(
                recipe.getId(),
                recipeIngredient.ingredientId().id()
        );
        this.recipe = recipe;
        this.baseQuantity = recipeIngredient.baseQuantity();
    }

    public RecipeIngredient toDomain() {
        return new RecipeIngredient(
                new IngredientId(id.getIngredientId()),
                baseQuantity
        );
    }

    public void setRecipe(JpaRecipeEntity recipe) {
        this.recipe = recipe;
    }
}
