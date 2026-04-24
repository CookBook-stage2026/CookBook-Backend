package cookbook.stage.backend.repository.jpa.user;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @ElementCollection
    @CollectionTable(
            name = "user_excluded_categories",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "category")
    private Set<Category> excludedCategories = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_excluded_ingredients",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "ingredient")
    private Set<UUID> excludedIngredientIds = new HashSet<>();

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
                user.getId().id(),
                user.getEmail(),
                user.getDisplayName(),
                user.getSocialConnections().stream()
                        .map(JpaSocialConnectionEntity::fromDomain)
                        .toList()
        );
    }

    public Set<Category> getExcludedCategories() {
        return excludedCategories;
    }

    public Set<UUID> getExcludedIngredientIds() {
        return excludedIngredientIds;
    }

    public void setExcludedCategories(Set<Category> excludedCategories) {
        this.excludedCategories.clear();
        this.excludedCategories.addAll(excludedCategories);
    }

    public void setExcludedIngredientIds(Set<UUID> excludedIngredientIds) {
        this.excludedIngredientIds.clear();
        this.excludedIngredientIds.addAll(excludedIngredientIds);
    }
}
