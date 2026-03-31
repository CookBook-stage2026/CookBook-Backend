package cookbook.stage.backend.recipe.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaRecipeRepository extends JpaRepository<JpaRecipeEntity, UUID> {
}
