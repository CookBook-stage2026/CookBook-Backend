package cookbook.stage.backend.repository.jpa.recipe;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaRecipeRepository extends JpaRepository<JpaRecipeEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"steps", "ingredients", "ingredients.ingredient"})
    Optional<JpaRecipeEntity> findById(@NonNull UUID id);

    @EntityGraph(attributePaths = {"steps", "ingredients", "ingredients.ingredient"})
    Optional<JpaRecipeEntity> findByIdAndUserId(UUID id, UUID userId);

    Page<JpaRecipeEntity> findByUserId(UUID userId, Pageable pageable);

    @Query("""
                SELECT r FROM JpaRecipeEntity r
                JOIN r.ingredients i
                WHERE i.id.ingredientId IN :ingredientIds
                    AND r.userId = :userId
                GROUP BY r.id
                HAVING COUNT(DISTINCT i.id.ingredientId) >= :idCount
            """)
    Page<JpaRecipeEntity> findByIngredientsAndCreatorId(
            @Param("ingredientIds") List<UUID> ingredientIds,
            @Param("idCount") int idCount,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM JpaRecipeEntity r
            WHERE (
                LOWER(r.name) LIKE LOWER(CONCAT(:name, '%'))
                OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))
            )
            AND r.userId = :userId
            ORDER BY
                CASE WHEN LOWER(r.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0 ELSE 1 END,
                r.name
            """)
    List<JpaRecipeEntity> searchByNamePrioritizingStartsWith(
            @RequestParam("name") String name, @RequestParam("userId") UUID userId, Pageable pageable);
}
