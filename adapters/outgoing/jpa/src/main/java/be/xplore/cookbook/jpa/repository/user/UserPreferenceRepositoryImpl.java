package be.xplore.cookbook.jpa.repository.user;

import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaIngredientEntity;
import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
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
