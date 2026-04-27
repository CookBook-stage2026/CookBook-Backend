package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.exception.UserNotFoundException;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferenceRepository;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.repository.jpa.ingredient.JpaIngredientEntity;
import cookbook.stage.backend.repository.jpa.user.JpaUserEntity;
import cookbook.stage.backend.repository.jpa.userpreference.JpaUserPreferenceRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

@Repository
public class UserPreferenceRepositoryImpl implements UserPreferenceRepository {

    private final JpaUserPreferenceRepository jpaUserPreferenceRepository;

    public UserPreferenceRepositoryImpl(JpaUserPreferenceRepository jpaUserPreferenceRepository) {
        this.jpaUserPreferenceRepository = jpaUserPreferenceRepository;
    }

    @Override
    public void updatePreferences(UserId userId, UserPreferences preferences) {
        JpaUserEntity entity = jpaUserPreferenceRepository.findWithPreferencesById(userId.id())
                .orElseThrow(UserNotFoundException::new);
        entity.setExcludedCategories(new HashSet<>(preferences.excludedCategories()));
        entity.setExcludedIngredients(preferences.excludedIngredients().stream()
                .map(JpaIngredientEntity::fromDomain)
                .collect(Collectors.toCollection(HashSet::new)));
        jpaUserPreferenceRepository.save(entity);
    }

    @Override
    public UserPreferences findPreferences(UserId userId) {
        JpaUserEntity entity = jpaUserPreferenceRepository.findWithPreferencesById(userId.id())
                .orElseThrow(UserNotFoundException::new);
        return new UserPreferences(
                new ArrayList<>(entity.getExcludedCategories()),
                entity.getExcludedIngredients().stream()
                        .map(JpaIngredientEntity::toDomainWithoutCategories)
                        .toList()
        );
    }
}
