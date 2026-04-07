package cookbook.stage.backend.user.domain;

import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialConnection(String provider, String providerId);
    Optional<User> findById(UserId id);
    User save(User user);
}
