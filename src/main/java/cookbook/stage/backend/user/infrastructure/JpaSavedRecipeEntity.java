package cookbook.stage.backend.user.infrastructure;

import cookbook.stage.backend.recipe.shared.RecipeId;
import cookbook.stage.backend.user.domain.SavedRecipe;
import cookbook.stage.backend.user.domain.UserId;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "saved_recipes")
@IdClass(JpaSavedRecipeEntity.SavedRecipeId.class)
public class JpaSavedRecipeEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Id
    @Column(name = "recipe_id", columnDefinition = "UUID")
    private UUID recipeId;

    protected JpaSavedRecipeEntity() {
    }

    public JpaSavedRecipeEntity(UUID userId, UUID recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    public static JpaSavedRecipeEntity fromDomain(SavedRecipe savedRecipe) {
        return new JpaSavedRecipeEntity(
                savedRecipe.userId().id(),
                savedRecipe.recipeId().id()
        );
    }

    public SavedRecipe toDomain() {
        return new SavedRecipe(
                new UserId(this.userId),
                new RecipeId(this.recipeId)
        );
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(UUID recipeId) {
        this.recipeId = recipeId;
    }

    public static class SavedRecipeId implements Serializable {
        private UUID userId;
        private UUID recipeId;

        public SavedRecipeId() {}

        public SavedRecipeId(UUID userId, UUID recipeId) {
            this.userId = userId;
            this.recipeId = recipeId;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public UUID getRecipeId() {
            return recipeId;
        }

        public void setRecipeId(UUID recipeId) {
            this.recipeId = recipeId;
        }
    }
}