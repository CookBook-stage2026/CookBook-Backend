package cookbook.stage.backend.repository.jpa.userpreference;

import cookbook.stage.backend.repository.jpa.user.JpaUserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserPreferenceRepository extends JpaRepository<JpaUserEntity, UUID> {

    @EntityGraph(attributePaths = {"excludedCategories", "excludedIngredients"})
    Optional<JpaUserEntity> findWithPreferencesById(UUID id);
}
