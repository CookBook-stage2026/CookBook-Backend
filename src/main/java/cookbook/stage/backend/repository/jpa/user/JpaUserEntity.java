package cookbook.stage.backend.repository.jpa.user;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class JpaUserEntity {
    @Id
    private UUID userId;

    @Column
    private String email;

    @Column
    private String displayName;

    @ElementCollection
    @CollectionTable(
            name = "social_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
    )
    private List<JpaSocialConnectionEntity> socialConnections = new ArrayList<>();

    protected JpaUserEntity() {
    }

    public JpaUserEntity(UUID userId, String email, String displayName,
                         List<JpaSocialConnectionEntity> socialConnections) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.socialConnections = socialConnections;
    }

    public User toDomain() {
        return new User(
                new UserId(this.userId),
                this.email,
                this.displayName,
                this.socialConnections.stream().map(JpaSocialConnectionEntity::toDomain).toList()
        );
    }

    public static JpaUserEntity fromDomain(User user) {
        return new JpaUserEntity(user.getId().id(), user.getEmail(), user.getDisplayName(),
                user.getSocialConnections().stream()
                        .map(JpaSocialConnectionEntity::fromDomain)
                        .toList());
    }
}
