package cookbook.stage.backend.user.domain;

import cookbook.stage.backend.user.shared.UserId;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UserId id);
}
