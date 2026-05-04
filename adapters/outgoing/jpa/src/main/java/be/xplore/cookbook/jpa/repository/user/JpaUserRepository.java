package be.xplore.cookbook.jpa.repository.user;

import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    @Query("""
        SELECT u FROM JpaUserEntity u
        WHERE u.provider = :provider AND u.providerId = :providerId
    """)
    Optional<JpaUserEntity> findBySocialConnection(
            @Param("provider") String provider,
            @Param("providerId") String providerId
    );
}
