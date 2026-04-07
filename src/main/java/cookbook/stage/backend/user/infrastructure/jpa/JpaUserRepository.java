package cookbook.stage.backend.user.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {
    Optional<JpaUserEntity> findByEmail(String email);

    @Query("""
        SELECT u FROM JpaUserEntity u
        JOIN u.socialConnections c
        WHERE c.provider = :provider AND c.providerId = :providerId
    """)
    Optional<JpaUserEntity> findBySocialConnection(
            @Param("provider") String provider,
            @Param("providerId") String providerId
    );
}
