package cookbook.stage.backend.recipe.infrastructure.jpa.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaRecipeRepository extends JpaRepository<JpaRecipeEntity, UUID> {

    @Query("""
                SELECT DISTINCT r FROM JpaRecipeEntity r
                LEFT JOIN FETCH r.ingredients
                WHERE r.id = :id
            """)
    Optional<JpaRecipeEntity> findByIdWithIngredients(UUID id);

    @Query("""
                SELECT DISTINCT r FROM JpaRecipeEntity r
                LEFT JOIN FETCH r.steps
                WHERE r.id = :id
            """)
    Optional<JpaRecipeEntity> findByIdWithSteps(UUID id);
}
