package cookbook.stage.backend.repository.jpa.ingredient;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaIngredientRepository extends JpaRepository<JpaIngredientEntity, UUID> {

    @Query("""
            SELECT i FROM JpaIngredientEntity i
            WHERE (
                LOWER(i.name) LIKE LOWER(CONCAT(:name, '%'))
                OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))
            )
            AND i.id NOT IN :excludedIds
            ORDER BY
                CASE WHEN LOWER(i.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0 ELSE 1 END,
                i.name
            """)
    List<JpaIngredientEntity> searchByNamePrioritizingStartsWith(
            @Param("name") String name,
            @Param("excludedIds") List<UUID> excludedIds,
            Pageable pageable);
}
