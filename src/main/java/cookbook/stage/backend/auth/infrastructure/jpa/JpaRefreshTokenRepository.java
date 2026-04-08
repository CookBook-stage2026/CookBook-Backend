package cookbook.stage.backend.auth.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<JpaRefreshTokenEntity, String> {
    Optional<JpaRefreshTokenEntity> findByToken(String token);

    @Modifying
    void deleteByUserId(UUID userId);
}
