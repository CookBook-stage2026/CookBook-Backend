package cookbook.stage.backend.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialConnection(String provider, String providerId);
    Optional<User> findById(UserId id);
    User save(User user);
    UserPreferences findPreferences(UserId userId);
    void updatePreferences(UserId userId, UserPreferences preferences);
}
