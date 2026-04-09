package cookbook.stage.backend.ingredient.infrastructure.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaIngredientRepository extends JpaRepository<JpaIngredientEntity, UUID> {
    List<JpaIngredientEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
