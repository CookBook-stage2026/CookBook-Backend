package cookbook.stage.backend.recipe.infrastructure.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaRecipeRepository extends JpaRepository<JpaRecipeEntity, UUID> {

    @Query("SELECT r FROM JpaRecipeEntity r LEFT JOIN FETCH r.steps LEFT JOIN FETCH r.ingredients")
    List<JpaRecipeEntity> findAllWithStepsAndIngredients(Pageable pageable);
}
