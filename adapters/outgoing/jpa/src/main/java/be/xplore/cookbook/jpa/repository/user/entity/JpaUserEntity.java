package be.xplore.cookbook.jpa.repository.user.entity;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.jpa.repository.recipe.entity.JpaRecipeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "social_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
    )
    private List<JpaSocialConnectionEntity> socialConnections = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaRecipeEntity> recipes = new ArrayList<>();

    protected JpaUserEntity() {
    }

    public JpaUserEntity(UUID userId, String email, String displayName,
                         List<JpaSocialConnectionEntity> socialConnections) {
        this.id = userId;
        this.email = email;
        this.displayName = displayName;
        this.socialConnections = socialConnections;
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
                user.id().id(),
                user.email(),
                user.displayName(),
                user.socialConnections().stream()
                        .map(JpaSocialConnectionEntity::fromDomain)
                        .toList()
        );
    }

    public UUID getId() {
        return id;
    }
}
