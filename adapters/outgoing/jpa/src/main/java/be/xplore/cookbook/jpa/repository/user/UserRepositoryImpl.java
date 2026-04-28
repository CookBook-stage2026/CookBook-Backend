package be.xplore.cookbook.jpa.repository.user;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
import org.springframework.stereotype.Repository;

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
    public Optional<User> findBySocialConnection(String provider, String providerId) {
        return jpaUserRepository.findBySocialConnection(provider, providerId).map(JpaUserEntity::toDomain);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findById(id.id()).map(JpaUserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(JpaUserEntity.fromDomain(user)).toDomain();
    }
}
