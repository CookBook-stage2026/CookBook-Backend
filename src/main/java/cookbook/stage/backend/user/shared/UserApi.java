package cookbook.stage.backend.user.shared;

import java.util.Optional;

public interface UserApi {
    Optional<User> findById(UserId id);
    Optional<User> findBySocialConnection(String provider, String providerId);
    User autoSaveAfterLogin(String email, String name, String provider, String providerId);
}
