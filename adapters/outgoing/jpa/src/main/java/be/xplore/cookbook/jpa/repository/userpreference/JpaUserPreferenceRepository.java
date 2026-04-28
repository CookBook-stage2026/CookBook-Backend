package be.xplore.cookbook.jpa.repository.userpreference;

import be.xplore.cookbook.jpa.repository.userpreference.entity.JpaUserPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserPreferenceRepository extends JpaRepository<JpaUserPreferencesEntity, UUID> {

    Optional<JpaUserPreferencesEntity> findByUserId(UUID userId);
}
