package cookbook.stage.backend.user.infrastructure;

import cookbook.stage.backend.user.domain.User;
import cookbook.stage.backend.user.domain.UserRepository;
import cookbook.stage.backend.user.infrastructure.jpa.JpaUserEntity;
import cookbook.stage.backend.user.infrastructure.jpa.JpaUserRepository;
import cookbook.stage.backend.user.shared.UserId;

import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(JpaUserEntity.fromDomain(user))
                .toDomain();
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findById(id.id()).map(JpaUserEntity::toDomain);
    }
}
