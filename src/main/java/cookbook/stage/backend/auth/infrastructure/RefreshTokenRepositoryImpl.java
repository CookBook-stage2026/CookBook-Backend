package cookbook.stage.backend.auth.infrastructure;

import cookbook.stage.backend.auth.domain.RefreshToken;
import cookbook.stage.backend.auth.domain.RefreshTokenRepository;
import cookbook.stage.backend.auth.infrastructure.jpa.JpaRefreshTokenEntity;
import cookbook.stage.backend.auth.infrastructure.jpa.JpaRefreshTokenRepository;
import cookbook.stage.backend.user.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final JpaRefreshTokenRepository jpaRepository;

    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(RefreshToken token) {
        jpaRepository.save(JpaRefreshTokenEntity.fromDomain(token));
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(JpaRefreshTokenEntity::toDomain);
    }

    @Override
    public void delete(String token) {
        jpaRepository.deleteById(token);
    }

    @Override
    public void deleteByUserId(UserId userId) {
        jpaRepository.deleteByUserId(userId.id());
    }
}
