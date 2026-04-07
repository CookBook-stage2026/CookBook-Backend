package cookbook.stage.backend.user.infrastructure.jpa;

import cookbook.stage.backend.user.domain.SocialConnection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(name = "social_connections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))

public class JpaSocialConnectionEntity {
    @Id
    private UUID id;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private JpaUserEntity user;

    protected JpaSocialConnectionEntity() {
    }

    private JpaSocialConnectionEntity(UUID id, String provider, String providerId, JpaUserEntity user) {
        this.id = id;
        this.provider = provider;
        this.providerId = providerId;
        this.user = user;
    }

    public SocialConnection toDomain() {
        return new SocialConnection(
                this.provider,
                this.providerId,
                this.user.toDomain()
        );
    }

    public static JpaSocialConnectionEntity fromDomain(SocialConnection domain, JpaUserEntity userEntity) {
        JpaSocialConnectionEntity entity = new JpaSocialConnectionEntity();
        entity.id = domain.getId();
        entity.provider = domain.getProvider();
        entity.providerId = domain.getProviderId();
        entity.user = userEntity;
        return entity;
    }
}
