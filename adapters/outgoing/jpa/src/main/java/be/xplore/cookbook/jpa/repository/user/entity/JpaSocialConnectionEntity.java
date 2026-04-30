package be.xplore.cookbook.jpa.repository.user.entity;

import be.xplore.cookbook.core.domain.user.SocialConnection;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class JpaSocialConnectionEntity {

    @Column(name = "id")
    private UUID id;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    protected JpaSocialConnectionEntity() {
    }

    private JpaSocialConnectionEntity(UUID id, String provider, String providerId) {
        this.id = id;
        this.provider = provider;
        this.providerId = providerId;
    }

    public SocialConnection toDomain() {
        return new SocialConnection(
                this.provider,
                this.providerId
        );
    }

    public static JpaSocialConnectionEntity fromDomain(SocialConnection domain) {
        return new JpaSocialConnectionEntity(domain.getId(), domain.getProvider(), domain.getProviderId());
    }
}
