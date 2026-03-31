package cookbook.stage.backend.user.infrastructure.jpa;

import cookbook.stage.backend.recipe.shared.RecipeId;
import cookbook.stage.backend.user.domain.SavedRecipe;
import cookbook.stage.backend.user.shared.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saved_recipes")
public class JpaSavedRecipeEntity {
    @EmbeddedId
    private JpaSavedRecipeId id;

    @Column(nullable = false)
    private Instant savedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private JpaUserEntity user;

    public JpaSavedRecipeEntity(UUID userId, UUID recipeId, Instant savedAt) {
        this.id = new JpaSavedRecipeId(userId, recipeId);
        this.savedAt = savedAt;
    }

    protected JpaSavedRecipeEntity() {
    }

    public static JpaSavedRecipeEntity fromDomain(SavedRecipe savedRecipe, JpaUserEntity user) {
        JpaSavedRecipeEntity jpaSavedRecipe = new JpaSavedRecipeEntity(
                savedRecipe.userId().id(),
                savedRecipe.recipeId().id(),
                savedRecipe.savedAt()
        );

        jpaSavedRecipe.user = user;
        return jpaSavedRecipe;
    }

    public SavedRecipe toDomain() {
        return new SavedRecipe(
                new UserId(id.getUserId()),
                new RecipeId(id.getRecipeId()),
                savedAt
        );
    }
}
