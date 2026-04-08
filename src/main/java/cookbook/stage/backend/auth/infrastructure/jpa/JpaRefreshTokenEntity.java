package cookbook.stage.backend.auth.infrastructure.jpa;

import cookbook.stage.backend.auth.domain.RefreshToken;
import cookbook.stage.backend.user.shared.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class JpaRefreshTokenEntity {

    @Id
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    protected JpaRefreshTokenEntity() {
    }

    public JpaRefreshTokenEntity(String token, UUID userId, Instant expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }
    public static JpaRefreshTokenEntity fromDomain(RefreshToken refreshToken) {
        return new JpaRefreshTokenEntity(
                refreshToken.token(),
                refreshToken.userId().id(),
                refreshToken.expiryDate()
        );
    }
    public RefreshToken toDomain() {
        return new RefreshToken(
                token,
                new UserId(userId),
                expiryDate
        );
    }
}
