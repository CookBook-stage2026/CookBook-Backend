package be.xplore.cookbook.jpa.repository.userpreference.entity;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaIngredientEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class JpaUserPreferencesEntity {

    @Id
    private UUID userId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_excluded_categories", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Category> excludedCategories = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_excluded_ingredients",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<JpaIngredientEntity> excludedIngredients = new HashSet<>();

    protected JpaUserPreferencesEntity() {
    }

    public UserPreferences toDomain() {
        return new UserPreferences(
                new UserId(userId),
                excludedCategories.stream().toList(),
                excludedIngredients.stream()
                        .map(JpaIngredientEntity::toDomainWithoutCategories)
                        .toList()
        );
    }

    public static JpaUserPreferencesEntity fromDomain(UserPreferences preferences) {
        JpaUserPreferencesEntity entity = new JpaUserPreferencesEntity();
        entity.userId = preferences.userId().id();
        entity.excludedCategories = new HashSet<>(preferences.excludedCategories());
        entity.excludedIngredients = preferences.excludedIngredients().stream()
                .map(JpaIngredientEntity::fromDomain)
                .collect(Collectors.toCollection(HashSet::new));
        return entity;
    }
}
