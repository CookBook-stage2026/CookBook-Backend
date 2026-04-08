package cookbook.stage.backend.auth.domain;

import cookbook.stage.backend.user.shared.UserId;
import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    void delete(String token);
    void deleteByUserId(UserId userId);
}
