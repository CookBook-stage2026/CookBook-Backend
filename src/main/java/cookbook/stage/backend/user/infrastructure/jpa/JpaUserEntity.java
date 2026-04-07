package cookbook.stage.backend.user.infrastructure.jpa;

import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class JpaUserEntity {
    @Id
    private UUID userId;

    @Column(name = "email")
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
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
        var jpaUser = new JpaUserEntity();
        jpaUser.userId = user.getId().id();
        jpaUser.socialConnections = user.getSocialConnections().stream()
                .map(sc -> JpaSocialConnectionEntity.fromDomain(sc, jpaUser))
                .toList();
        jpaUser.email = user.getEmail();
        jpaUser.displayName = user.getDisplayName();
        return jpaUser;
    }
}
