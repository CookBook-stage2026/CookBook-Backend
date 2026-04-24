package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.domain.user.UserRepository;
import cookbook.stage.backend.repository.jpa.ingredient.JpaIngredientEntity;
import cookbook.stage.backend.repository.jpa.user.JpaUserEntity;
import cookbook.stage.backend.repository.jpa.user.JpaUserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(JpaUserEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findBySocialConnection(String provider, String providerId) {
        return jpaUserRepository.findBySocialConnection(provider, providerId).map(JpaUserEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findById(id.id()).map(JpaUserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(JpaUserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public void updatePreferences(UserId userId, UserPreferences preferences) {
        JpaUserEntity entity = jpaUserRepository.findWithPreferencesById(userId.id())
                .orElseThrow(userId::notFound);
        entity.setExcludedCategories(new HashSet<>(preferences.excludedCategories()));
        entity.setExcludedIngredients(preferences.excludedIngredients().stream()
                .map(JpaIngredientEntity::fromDomain)
                .collect(Collectors.toCollection(HashSet::new)));
        jpaUserRepository.save(entity);
    }

    @Override
    public UserPreferences findPreferences(UserId userId) {
        JpaUserEntity entity = jpaUserRepository.findWithPreferencesById(userId.id())
                .orElseThrow(userId::notFound);
        return new UserPreferences(
                new ArrayList<>(entity.getExcludedCategories()),
                entity.getExcludedIngredients().stream()
                        .map(JpaIngredientEntity::toDomain)
                        .toList()
        );
    }
}
