package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findBySocialConnection(String provider, String providerId);

    Optional<User> findById(UserId id);

    User save(User user);
}
