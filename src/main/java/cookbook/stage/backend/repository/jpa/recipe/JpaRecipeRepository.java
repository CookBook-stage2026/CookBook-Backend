package cookbook.stage.backend.repository.jpa.recipe;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaRecipeRepository extends JpaRepository<JpaRecipeEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"steps", "ingredients", "ingredients.ingredient"})
    Optional<JpaRecipeEntity> findById(@NonNull UUID id);

    @Query("""
                SELECT r FROM JpaRecipeEntity r
                JOIN r.ingredients i
                WHERE i.id.ingredientId IN :ingredientIds
                GROUP BY r.id
                HAVING COUNT(DISTINCT i.id.ingredientId) >= :idCount
            """)
    Page<JpaRecipeEntity> findByIngredients(
            @Param("ingredientIds") List<UUID> ingredientIds,
            @Param("idCount") int idCount,
            Pageable pageable
    );
}
