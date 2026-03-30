package cookbook.stage.backend.recipe.infrastructure;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "recipe_ingredients")
public class JpaRecipeIngredientEntity {
    @Entity
    @Table(name = "recipe_ingredients")
    public class JpaRecipeIngredientEntity {


        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "id")
        private UUID id;

        // Notice we set insertable/updatable to false.
        // This is because the parent JpaRecipeEntity's @JoinColumn manages this foreign key!
        @Column(name = "recipe_id", nullable = false, insertable = false, updatable = false)
        private UUID recipeId;

        @Column(name = "ingredient_id", nullable = false)
        private UUID ingredientId;

        @Column(name = "is_scalable", nullable = false)
        private boolean isScalable;

        @Column(name = "base_quantity", nullable = false)
        private double baseQuantity;

        public JpaRecipeIngredientEntity(UUID recipeId, UUID ingredientId, boolean isScalable, double baseQuantity) {
            this.recipeId = recipeId;
            this.ingredientId = ingredientId;
            this.isScalable = isScalable;
            this.baseQuantity = baseQuantity;
        }

        protected JpaRecipeIngredientEntity() {
        } // for JPA

        public static JpaRecipeIngredientEntity fromDomain(RecipeIngredient recipeIngredient) {
            return new JpaRecipeIngredientEntity(
                    recipeIngredient.recipeId().id(),
                    recipeIngredient.ingredientId().id(),
                    recipeIngredient.isScalable(),
                    recipeIngredient.baseQuantity()
            );
        }

        public RecipeIngredient toDomain() {
            return new RecipeIngredient(
                    new RecipeId(this.recipeId),
                    new IngredientId(this.ingredientId),
                    this.isScalable,
                    this.baseQuantity
            );
        }
}
