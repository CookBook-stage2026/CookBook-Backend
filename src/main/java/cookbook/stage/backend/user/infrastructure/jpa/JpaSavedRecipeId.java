package cookbook.stage.backend.user.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class JpaSavedRecipeId implements Serializable {
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID recipeId;

    public JpaSavedRecipeId(UUID userId, UUID recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    protected JpaSavedRecipeId() {
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getRecipeId() {
        return recipeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof JpaSavedRecipeId other)) {
            return false;
        }

        return Objects.equals(userId, other.userId) && Objects.equals(recipeId, other.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, recipeId);
    }
}
