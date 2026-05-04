package be.xplore.cookbook.jpa.repository.user.entity;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class JpaUserEntity {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column
    private String email;

    @Column
    private String displayName;

    @Column
    private String provider;

    @Column
    private String providerId;

    protected JpaUserEntity() {
    }

    public JpaUserEntity(UUID userId, String email, String displayName,
                         String provider, String providerId) {
        this.id = userId;
        this.email = email;
        this.displayName = displayName;
        this.provider = provider;
        this.providerId = providerId;
    }

    public User toDomain() {
        return new User(
                new UserId(this.id),
                this.email,
                this.displayName,
                this.provider,
                this.providerId
        );
    }

    public static JpaUserEntity fromDomain(User user) {
        return new JpaUserEntity(
                user.id().id(),
                user.email(),
                user.displayName(),
                user.provider(),
                user.providerId()
        );
    }

    public UUID getId() {
        return id;
    }
}
