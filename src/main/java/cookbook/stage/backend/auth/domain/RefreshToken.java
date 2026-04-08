package cookbook.stage.backend.auth.domain;

import cookbook.stage.backend.user.shared.UserId;
import java.time.Instant;

public record RefreshToken(String token, UserId userId, Instant expiryDate) {
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
