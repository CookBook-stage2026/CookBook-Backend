package cookbook.stage.backend.repository.jpa.user;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;
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

    @ElementCollection
    @CollectionTable(
            name = "social_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
    )
    private List<JpaSocialConnectionEntity> socialConnections = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaRecipeEntity> recipes = new ArrayList<>();

    protected JpaUserEntity() {
    }

    public JpaUserEntity(UUID userId, String email, String displayName,
                         List<JpaSocialConnectionEntity> socialConnections, List<JpaRecipeEntity> recipes) {
        this.id = userId;
        this.email = email;
        this.displayName = displayName;
        this.socialConnections = socialConnections;
        this.recipes = recipes;
    }

    public User toDomain() {
        return new User(
                new UserId(this.id),
                this.email,
                this.displayName,
                this.socialConnections.stream().map(JpaSocialConnectionEntity::toDomain).toList()
        );
    }

    public static JpaUserEntity fromDomain(User user) {
        return new JpaUserEntity(
                user.getId().id(),
                user.getEmail(),
                user.getDisplayName(),
                user.getSocialConnections().stream()
                        .map(JpaSocialConnectionEntity::fromDomain)
                        .toList()
        ,
                user.getRecipes().stream()
                        .map(JpaRecipeEntity::fromDomain)
                        .toList()
        );
    }
}
