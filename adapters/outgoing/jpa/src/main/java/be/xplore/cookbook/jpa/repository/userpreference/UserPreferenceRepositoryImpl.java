package be.xplore.cookbook.jpa.repository.userpreference;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.jpa.repository.userpreference.entity.JpaUserPreferencesEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserPreferenceRepositoryImpl implements UserPreferenceRepository {

    private final JpaUserPreferenceRepository jpaUserPreferenceRepository;

    public UserPreferenceRepositoryImpl(JpaUserPreferenceRepository jpaUserPreferenceRepository) {
        this.jpaUserPreferenceRepository = jpaUserPreferenceRepository;
    }

    @Override
    public UserPreferences save(UserPreferences preferences) {
        return jpaUserPreferenceRepository.save(JpaUserPreferencesEntity.fromDomain(preferences))
                .toDomain();
    }

    @Override
    public Optional<UserPreferences> findPreferences(UserId userId) {
        return jpaUserPreferenceRepository
                .findByUserId(userId.id())
                .map(JpaUserPreferencesEntity::toDomain);
    }
}
