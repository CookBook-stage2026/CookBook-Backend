package cookbook.stage.backend.user.infrastructure;

import cookbook.stage.backend.user.domain.UserRepository;
import cookbook.stage.backend.user.infrastructure.jpa.JpaUserEntity;
import cookbook.stage.backend.user.infrastructure.jpa.JpaUserRepository;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
}
